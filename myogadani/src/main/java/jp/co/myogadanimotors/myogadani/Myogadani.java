package jp.co.myogadanimotors.myogadani;

import jp.co.myogadanimotors.myogadani.common.Constants;
import jp.co.myogadanimotors.myogadani.config.ConfigAccessor;
import jp.co.myogadanimotors.myogadani.emsadapter.IEmsAdapter;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.RequestIdGenerator;
import jp.co.myogadanimotors.myogadani.exchangeadapter.IExchangeAdapter;
import jp.co.myogadanimotors.myogadani.marketdataprovider.IMarketDataProvider;
import jp.co.myogadanimotors.myogadani.ordermanagement.OrderManager;
import jp.co.myogadanimotors.myogadani.store.master.MasterDataInitializeException;
import jp.co.myogadanimotors.myogadani.store.master.extendedattriute.ExtendedAttributeMaster;
import jp.co.myogadanimotors.myogadani.store.master.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.master.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.store.master.strategy.StrategyMaster;
import jp.co.myogadanimotors.myogadani.strategy.IStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContextFactory;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;
import jp.co.myogadanimotors.myogadani.timesource.ITimerEventSource;
import jp.co.myogadanimotors.myogadani.timesource.SystemTimeSource;
import jp.co.myogadanimotors.myogadani.timesource.TimerEventSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static java.util.Objects.requireNonNull;

public final class Myogadani implements Runnable {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final String environment;
    private final IStrategyFactory strategyFactory;

    private final int RUNNING = 0;
    private final int TERMINATED = 1;
    private final AtomicInteger state = new AtomicInteger(RUNNING);

    private ExecutorService eventQueue;
    private ExecutorService[] strategyEventQueues;

    private IEmsAdapter emsAdapter;
    private IExchangeAdapter exchangeAdapter;
    private IMarketDataProvider marketDataProvider;
    private ITimerEventSource timerEventSource;
    private OrderManager orderManager;

    public Myogadani(String environment, IStrategyFactory strategyFactory) {
        this.environment = (environment != null) ? environment : "development";
        this.strategyFactory = requireNonNull(strategyFactory);
    }

    private boolean init() {
        // todo: initialize logger
        Logger logger = LogManager.getLogger(getClass().getName());

        logger.info("initializing Myogadani. (environment: {})", environment);

        // initialize config accessor
        ConfigAccessor configAccessor = new ConfigAccessor();
        try {
            configAccessor.parse(environment, getClass().getClassLoader().getResource(Constants.CONFIG_FILE_NAME));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        String strategyConfigLocation = configAccessor.getString("myogadani.strategyConfig.jsonFileLocation");
        ConfigAccessor strategyConfigAccessor = new ConfigAccessor();
        try {
            strategyConfigAccessor.parse(environment, new File(strategyConfigLocation));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        // initialize master data stores
        MarketMaster marketMaster = new MarketMaster();
        ProductMaster productMaster = new ProductMaster();
        StrategyMaster strategyMaster = new StrategyMaster();
        ExtendedAttributeMaster extendedAttributeMaster = new ExtendedAttributeMaster();
        try {
            marketMaster.init(configAccessor);
            productMaster.init(configAccessor);
            strategyMaster.init(configAccessor);
            extendedAttributeMaster.init(configAccessor);
        } catch (MasterDataInitializeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        // create event queues
        int numberOfStrategyThreads = configAccessor.getInt("myogadani.numberOfStrategyThreads", Constants.DEFAULT_NUMBER_OF_STRATEGY_THREADS);

        logger.info("creating executor service. (numberOfStrategyThreads: {})", numberOfStrategyThreads);

        eventQueue = Executors.newSingleThreadExecutor();
        strategyEventQueues = new ExecutorService[numberOfStrategyThreads];
        for (int i = 0; i < numberOfStrategyThreads; i++) {
            strategyEventQueues[i] = Executors.newSingleThreadExecutor();
        }

        // create ID generators
        EventIdGenerator eventIdGenerator = new EventIdGenerator(0);
        RequestIdGenerator requestIdGenerator = new RequestIdGenerator(0);

        // create time source
        ITimeSource timeSource = new SystemTimeSource();

        try {
            // create strategy context factory
            StrategyContextFactory strategyContextFactory = new StrategyContextFactory(
                    eventIdGenerator,
                    requestIdGenerator,
                    timeSource,
                    marketMaster,
                    productMaster
            );
            logger.info("strategy context factory created.");

            // create EMS adapter
            emsAdapter = null;  // todo: to be implemented
            logger.info("EMS adapter created.");

            // create Exchange adapter
            exchangeAdapter = null;    // todo: to be implemented
            logger.info("exchange adapter created.");

            // create market data provider
            marketDataProvider = null;  // todo: to be implemented
            logger.info("market data adapter created.");

            // create timer event source
            timerEventSource = new TimerEventSource(eventIdGenerator, timeSource, 0, eventQueue);
            logger.info("timer event source created.");

            // create order manager
            orderManager = new OrderManager(
                    eventIdGenerator,
                    timeSource,
                    strategyContextFactory,
                    strategyFactory,
                    marketMaster,
                    productMaster,
                    extendedAttributeMaster,
                    strategyMaster,
                    strategyConfigAccessor,
                    eventQueue,
                    strategyEventQueues
            );
            logger.info("order manager created.");

            // add event listeners
            orderManager.addEventListeners(emsAdapter, emsAdapter, exchangeAdapter);
            emsAdapter.addEventListener(orderManager);
            exchangeAdapter.addEventListeners(orderManager, orderManager);
            marketDataProvider.addEventListener(null);  // todo: to be implemented
            timerEventSource.addEventListener(orderManager);
            strategyContextFactory.addEventListeners(orderManager, orderManager, orderManager, marketDataProvider, timerEventSource);
            logger.info("event listeners added.");

        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        logger.info("initialization succeeded.");

        return true;
    }

    @Override
    public void run() {
        state.set(RUNNING);

        logger.info("starting Myogadani.");

        // initialize
        if (!init()) {
            logger.error("initialization failed.");
            state.set(TERMINATED);
            return;
        }

        // create executor service
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // start Myogadani
        executorService.execute(emsAdapter);
        executorService.execute(exchangeAdapter);
        executorService.execute(marketDataProvider);
        executorService.execute(timerEventSource);

        logger.info("Myogadani started.");

        while (state.get() == RUNNING) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Myogadani interrupted.", e);
            }
        }

        // shutdown
        emsAdapter.shutdown();
        exchangeAdapter.shutdown();
        marketDataProvider.shutdown();
        timerEventSource.shutdown();
        executorService.shutdown();
        eventQueue.shutdown();
        for (ExecutorService strategyEventQueue : strategyEventQueues) {
            strategyEventQueue.shutdown();
        }

        try {
            long timeOut = 1000;

            if (!emsAdapter.awaitTermination(timeOut)) logger.error("EMS adapter termination failed.");
            if (!exchangeAdapter.awaitTermination(timeOut)) logger.error("exchange adapter termination failed.");
            if (!marketDataProvider.awaitTermination(timeOut)) logger.error("market data provider termination failed.");
            if (!timerEventSource.awaitTermination(timeOut)) logger.error("timer event source termination failed.");

            if (!executorService.awaitTermination(timeOut, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }

            if (!eventQueue.awaitTermination(timeOut, TimeUnit.MILLISECONDS)) {
                eventQueue.shutdownNow();
            }

            for (ExecutorService strategyEventQueue : strategyEventQueues) {
                if (!strategyEventQueue.awaitTermination(timeOut, TimeUnit.MILLISECONDS)) {
                    strategyEventQueue.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Myogadani terminated. (environment: {})", environment);

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
