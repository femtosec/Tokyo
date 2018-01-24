package jp.co.myogadanimotors.myogadani;

import jp.co.myogadanimotors.itabashi.strategy.StrategyFactory;
import jp.co.myogadanimotors.myogadani.common.Constants;
import jp.co.myogadanimotors.myogadani.config.ConfigAccessor;
import jp.co.myogadanimotors.myogadani.emsadapter.IEmsAdapter;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.RequestIdGenerator;
import jp.co.myogadanimotors.myogadani.exchangeadapter.IExchangeAdapter;
import jp.co.myogadanimotors.myogadani.ordermanagement.OrderManager;
import jp.co.myogadanimotors.myogadani.ordermanagement.OrderValidator;
import jp.co.myogadanimotors.myogadani.store.masterdata.MasterDataInitializeException;
import jp.co.myogadanimotors.myogadani.store.masterdata.extendedattriute.ExtendedAttributeMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.StrategyMaster;
import jp.co.myogadanimotors.myogadani.strategy.IStrategyFactory;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;
import jp.co.myogadanimotors.myogadani.timesource.SystemTimeSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Myogadani implements Runnable {

    // environment
    private final String environment;

    private Myogadani(String environment) {
        this.environment = environment;
    }

    @Override
    public void run() {
        // todo: initialize logger
        Logger logger = LogManager.getLogger(getClass().getName());

        logger.info("starting Myogadani. (environment: {})", environment);

        // initialize config accessor
        ConfigAccessor configAccessor = new ConfigAccessor();
        try {
            configAccessor.parse(environment, Constants.CONFIG_FILE_NAME);
        } catch (FileNotFoundException e) {
            logger.error("file not found exception.", e);
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
            logger.error("master data initialize exception.", e);
            return;
        }

        try {
            // create order validator
            OrderValidator orderValidator = new OrderValidator(marketMaster, productMaster, extendedAttributeMaster);

            // create executor services
            int numberOfStrategyThreads = configAccessor.getInt("myogadani.numberOfStrategyThreads", Constants.DEFAULT_NUMBER_OF_STRATEGY_THREADS);

            logger.info("creating executor service. (numberOfStrategyThreads: {})", numberOfStrategyThreads);

            ExecutorService eventExecutor = Executors.newSingleThreadExecutor();
            ExecutorService[] strategyExecutors = new ExecutorService[numberOfStrategyThreads];
            for (int i = 0; i < numberOfStrategyThreads; i++) {
                strategyExecutors[i] = Executors.newSingleThreadExecutor();
            }

            // create ID generators
            EventIdGenerator eventIdGenerator = new EventIdGenerator(0);
            RequestIdGenerator requestIdGenerator = new RequestIdGenerator(0);

            // create adapters
            IEmsAdapter emsAdapter = null;  // todo: to be implemented
            IExchangeAdapter exchangeAdapter = null;    // todo: to be implemented
            ITimeSource timeSource = new SystemTimeSource();

            // create order manager
            OrderManager orderManager = new OrderManager(
                    emsAdapter,
                    exchangeAdapter,
                    eventIdGenerator,
                    timeSource,
                    orderValidator,
                    eventExecutor,
                    strategyExecutors);

            // create strategy factory
            IStrategyFactory strategyFactory = new StrategyFactory(
                    eventIdGenerator,
                    requestIdGenerator,
                    timeSource,
                    marketMaster,
                    productMaster,
                    strategyMaster,
                    orderManager,
                    orderManager,
                    orderManager,
                    null,   // todo: to be implemented
                    null    // todo: to be implemented
            );
            orderManager.setStrategyFactory(strategyFactory);
        } catch (NullPointerException e) {
            logger.error("null pointer exception.", e);
        }

        // todo: implement event loop

        logger.info("Myogadani terminated. (environment: {})", environment);
    }

    public static void main(String args[]) {
        Myogadani myogadani = new Myogadani(args[0]);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(myogadani);
        executorService.shutdown();
    }
}
