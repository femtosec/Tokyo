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

import static java.util.Objects.requireNonNull;

public class Myogadani implements Runnable {

    private final String environment;
    private final IStrategyFactory strategyFactory;

    public Myogadani(String environment, IStrategyFactory strategyFactory) {
        this.environment = (environment != null) ? environment : "development";
        this.strategyFactory = requireNonNull(strategyFactory);
    }

    @Override
    public void run() {
        // todo: initialize logger
        Logger logger = LogManager.getLogger(getClass().getName());

        logger.info("starting Myogadani. (environment: {})", environment);

        // initialize config accessor
        ConfigAccessor configAccessor = new ConfigAccessor();
        try {
            configAccessor.parse(environment, getClass().getClassLoader().getResource(Constants.CONFIG_FILE_NAME));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        String strategyConfigLocation = configAccessor.getString("myogadani.strategyConfig.jsonFileLocation");
        ConfigAccessor strategyConfigAccessor = new ConfigAccessor();
        try {
            strategyConfigAccessor.parse(environment, new File(strategyConfigLocation));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return;
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
            return;
        }

        try {
            // create event queues
            int numberOfStrategyThreads = configAccessor.getInt("myogadani.numberOfStrategyThreads", Constants.DEFAULT_NUMBER_OF_STRATEGY_THREADS);

            logger.info("creating executor service. (numberOfStrategyThreads: {})", numberOfStrategyThreads);

            ExecutorService eventQueue = Executors.newSingleThreadExecutor();
            ExecutorService[] strategyEventQueues = new ExecutorService[numberOfStrategyThreads];
            for (int i = 0; i < numberOfStrategyThreads; i++) {
                strategyEventQueues[i] = Executors.newSingleThreadExecutor();
            }

            // create ID generators
            EventIdGenerator eventIdGenerator = new EventIdGenerator(0);
            RequestIdGenerator requestIdGenerator = new RequestIdGenerator(0);

            // create time source
            ITimeSource timeSource = new SystemTimeSource();

            // create event sources
            IEmsAdapter emsAdapter = null;  // todo: to be implemented
            IExchangeAdapter exchangeAdapter = null;    // todo: to be implemented
            IMarketDataProvider marketDataProvider = null;  // todo: to be implemented
            ITimerEventSource timerEventSource = new TimerEventSource(eventIdGenerator, timeSource, 0, eventQueue);

            // create order manager
            logger.info("creating order manager.");

            OrderManager orderManager = new OrderManager(
                    eventIdGenerator,
                    requestIdGenerator,
                    timeSource,
                    strategyFactory,
                    marketMaster,
                    productMaster,
                    extendedAttributeMaster,
                    strategyMaster,
                    strategyConfigAccessor,
                    eventQueue,
                    strategyEventQueues
            );

            // add event listeners
            orderManager.addEventListeners(emsAdapter, emsAdapter, exchangeAdapter);
            emsAdapter.addEventListener(orderManager);
            exchangeAdapter.addFillListener(orderManager);
            timerEventSource.addEventListener(orderManager);

        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
        }

        // todo: implement event loop

        logger.info("Myogadani terminated. (environment: {})", environment);
    }
}
