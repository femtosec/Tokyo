package jp.co.myogadanimotors.yushima;

import groovyjarjarcommonscli.*;
import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.master.MasterDataInitializeException;
import jp.co.myogadanimotors.bunkyo.master.market.MarketMaster;
import jp.co.myogadanimotors.bunkyo.master.product.ProductMaster;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.bunkyo.timesource.SystemTimeSource;
import jp.co.myogadanimotors.yushima.mdmanager.MarketDataManager;
import jp.co.myogadanimotors.yushima.publisher.*;
import jp.co.myogadanimotors.yushima.subscriber.bitflyer.BitFlyerSubscriber;
import jp.co.myogadanimotors.yushima.subscriber.zaif.ZaifSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class Yushima implements Runnable {
    public static void main(String args[]) {
        // parse command line
        Options opt = new Options()
                .addOption("h", "help", false, "print help for this application")
                .addOption("e", "environment", true, "specify execution environment. default=development");
        CommandLine cl;
        try {
            cl = new BasicParser().parse(opt, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        String environment = null;
        if (cl.hasOption("h")) {
            new HelpFormatter().printHelp("usage", opt);
            return;
        } else if (cl.hasOption("e")) {
            environment = cl.getOptionValue("e");
        }

        // todo: initialize logger

        Yushima yushima = new Yushima(environment);

        // run Yushima
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(yushima);
        executorService.shutdown();
    }

    private final int DEFAULT_MAX_NUMBER_OF_DEPTHS = 10;

    private final Logger logger = LogManager.getLogger(getClass().getName());
    private final String environment;

    private final int RUNNING = 0;
    private final int TERMINATED = 1;
    private final AtomicInteger state = new AtomicInteger(RUNNING);

    private ExecutorService eventQueue;

    private JmsMarketDataPublisherFactory marketDataPublisherFactory;

    private BitFlyerSubscriber bitFlyerSubscriber;
    private ZaifSubscriber zaifSubscriber;

    private Yushima(String environment) {
        this.environment = environment;
    }

    private boolean init() {
        logger.info("initializing Kohinata. (environment: {})", environment);

        // initialize config accessor
        ConfigAccessor configAccessor = new ConfigAccessor();
        try {
            configAccessor.parse(environment, getClass().getClassLoader().getResource("configuration.groovy"));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        // initialize master data stores
        MarketMaster marketMaster = new MarketMaster();
        ProductMaster productMaster = new ProductMaster();
        try {
            marketMaster.init(configAccessor);
            productMaster.init(configAccessor);
        } catch (MasterDataInitializeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        // create event queues
        eventQueue = Executors.newSingleThreadExecutor();

        // create ID generator
        EventIdGenerator eventIdGenerator = new EventIdGenerator(0);

        // create time source
        ITimeSource timeSource = new SystemTimeSource();

        // create market data publisher factory
        marketDataPublisherFactory = new JmsMarketDataPublisherFactory();
        try {
            marketDataPublisherFactory.init(configAccessor);
        } catch (MarketDataPublisherException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        try {
            // create bitFlyer subscriber
            bitFlyerSubscriber = new BitFlyerSubscriber(eventIdGenerator, timeSource, marketMaster, productMaster);
            logger.info("bitFlyer subscriber created.");

            // create zaif subscriber
            zaifSubscriber = new ZaifSubscriber(eventIdGenerator, timeSource, marketMaster, productMaster);
            logger.info("Zaif subscriber created.");

            // create market data manager
            int maxNumberOfDepths = configAccessor.getInt("yushima.maxNumberOfDepths", DEFAULT_MAX_NUMBER_OF_DEPTHS);
            MarketDataManager marketDataManager = new MarketDataManager(marketDataPublisherFactory, maxNumberOfDepths, eventQueue);
            logger.info("market data manager created.");

            // add event listeners
            bitFlyerSubscriber.addEventListener(marketDataManager);
            zaifSubscriber.addEventListener(marketDataManager);
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        try {
            // initialize subscribers
            bitFlyerSubscriber.init(configAccessor);
            zaifSubscriber.init(configAccessor);
        } catch (MasterDataInitializeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        logger.info("initialization succeeded.");

        return true;
    }

    @Override
    public void run() {
        state.set(RUNNING);

        logger.info("starting Yushima.");

        // initialize
        if (!init()) {
            logger.error("initialization failed.");
            state.set(TERMINATED);
            return;
        }

        // connect
        bitFlyerSubscriber.connect();
        zaifSubscriber.connect();

        logger.info("Yushima started.");

        while (state.get() == RUNNING) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Yushima interrupted.", e);
            }
        }

        // disconnect
        bitFlyerSubscriber.disconnect();
        zaifSubscriber.disconnect();

        // shutdown
        eventQueue.shutdown();
        try {
            eventQueue.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            marketDataPublisherFactory.close();
        } catch (MarketDataPublisherException e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Yushima terminated. (environment: {})", environment);

        state.set(TERMINATED);
    }


    public void shutdown() {
        int TERMINATING = 2;
        state.set(TERMINATING);
        logger.info("terminating.");
    }

    public boolean awaitTermination(long timeOut) {
        if (state.get() == TERMINATED) {
            return true;
        }

        if (timeOut > 0) {
            try {
                logger.info("awaiting termination. (timeOut: {})", timeOut);
                sleep(timeOut);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                return false;
            }

            return awaitTermination(0);
        }

        return false;
    }
}
