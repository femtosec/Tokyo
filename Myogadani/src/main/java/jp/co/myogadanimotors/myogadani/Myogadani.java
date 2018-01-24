package jp.co.myogadanimotors.myogadani;

import emsadapter.IEmsAdapter;
import exchangeadapter.IExchangeAdapter;
import jp.co.myogadanimotors.itabashi.strategy.StrategyFactory;
import jp.co.myogadanimotors.myogadani.common.Constants;
import jp.co.myogadanimotors.myogadani.config.ConfigAccessor;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.RequestIdGenerator;
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

    // logger
    private final Logger logger = LogManager.getLogger(getClass().getName());

    // environment
    private final String environment;

    // config accessor
    private final ConfigAccessor configAccessor = new ConfigAccessor();

    // master data stores
    private final MarketMaster marketMaster = new MarketMaster();
    private final ProductMaster productMaster = new ProductMaster();
    private final StrategyMaster strategyMaster = new StrategyMaster();
    private final ExtendedAttributeMaster extendedAttributeMaster = new ExtendedAttributeMaster();

    // time source
    private final ITimeSource timeSource = new SystemTimeSource();

    // ID generators
    private final EventIdGenerator eventIdGenerator = new EventIdGenerator(0);
    private final RequestIdGenerator requestIdGenerator = new RequestIdGenerator(0);

    // adapters
    private final IEmsAdapter emsAdapter;
    private final IExchangeAdapter exchangeAdapter;

    private Myogadani(String environment) {
        this.environment = environment;
        this.emsAdapter = null; //todo: to be implemented
        this.exchangeAdapter = null;    // todo: to be implemented
    }

    @Override
    public void run() {
        // todo: initialize logger

        // initialize config accessor
        try {
            configAccessor.parse(environment, Constants.CONFIG_FILE_NAME);
        } catch (FileNotFoundException e) {
            logger.error(e);
            return;
        }

        // initialize master data stores
        try {
            marketMaster.init(configAccessor);
            productMaster.init(configAccessor);
            strategyMaster.init(configAccessor);
            extendedAttributeMaster.init(configAccessor);
        } catch (MasterDataInitializeException e) {
            logger.error(e);
            return;
        }

        // create order validator
        OrderValidator orderValidator = new OrderValidator(marketMaster, productMaster, extendedAttributeMaster);

        // create executor services
        int numberOfStrategyThreads = configAccessor.getInt("myogadani.numberOfStrategyThreads", Constants.DEFAULT_NUMBER_OF_STRATEGY_THREADS);
        ExecutorService eventExecutor = Executors.newSingleThreadExecutor();
        ExecutorService[] strategyExecutors = new ExecutorService[numberOfStrategyThreads];
        for (int i = 0; i < numberOfStrategyThreads; i++) {
            strategyExecutors[i] = Executors.newSingleThreadExecutor();
        }

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

        // todo: implement event loop
    }

    public static void main(String args[]) {
        Myogadani myogadani = new Myogadani(args[0]);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(myogadani);
        executorService.shutdown();
    }
}
