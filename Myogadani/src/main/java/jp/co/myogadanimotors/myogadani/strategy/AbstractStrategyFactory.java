package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.RequestIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.marketdata.IAsyncMarketDataRequestListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.IAsyncOrderListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncFillListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncReportListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.IAsyncTimerRegistrationListener;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.IProduct;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.StrategyMaster;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public abstract class AbstractStrategyFactory implements IStrategyFactory {

    private final EventIdGenerator eventIdGenerator;
    private final RequestIdGenerator requestIdGenerator;
    private final ITimeSource timeSource;
    private final MarketMaster marketMaster;
    private final ProductMaster productMaster;
    private final StrategyMaster strategyMaster;
    private final IAsyncOrderListener asyncOrderListener;
    private final IAsyncReportListener asyncReportListener;
    private final IAsyncFillListener asyncFillListener;
    private final IAsyncMarketDataRequestListener asyncMarketDataRequestListener;
    private final IAsyncTimerRegistrationListener asyncTimerRegistrationListener;

    public AbstractStrategyFactory(EventIdGenerator eventIdGenerator,
                                   RequestIdGenerator requestIdGenerator,
                                   ITimeSource timeSource,
                                   MarketMaster marketMaster,
                                   ProductMaster productMaster,
                                   StrategyMaster strategyMaster,
                                   IAsyncOrderListener asyncOrderListener,
                                   IAsyncReportListener asyncReportListener,
                                   IAsyncFillListener asyncFillListener,
                                   IAsyncMarketDataRequestListener asyncMarketDataRequestListener,
                                   IAsyncTimerRegistrationListener asyncTimerRegistrationListener) {
        this.eventIdGenerator = eventIdGenerator;
        this.requestIdGenerator = requestIdGenerator;
        this.timeSource = timeSource;
        this.marketMaster = marketMaster;
        this.productMaster = productMaster;
        this.strategyMaster = strategyMaster;
        this.asyncOrderListener = asyncOrderListener;
        this.asyncReportListener = asyncReportListener;
        this.asyncFillListener = asyncFillListener;
        this.asyncMarketDataRequestListener = asyncMarketDataRequestListener;
        this.asyncTimerRegistrationListener = asyncTimerRegistrationListener;
    }

    protected final StrategyContext createStrategyContext(IOrder order) {
        if (order == null) {
            throw new NullPointerException("IOrder is null.");
        }

        IMarket market = marketMaster.getByMic(order.getMic());
        IProduct product = productMaster.getBySymbol(order.getSymbol());
        return new StrategyContext(
                eventIdGenerator,
                requestIdGenerator,
                timeSource,
                order,
                market,
                product,
                asyncOrderListener,
                asyncReportListener,
                asyncFillListener,
                asyncMarketDataRequestListener,
                asyncTimerRegistrationListener
        );
    }

    protected final IStrategyDescriptor getStrategyDescriptor(String strategyName) {
        IStrategyDescriptor strategyDescriptor = strategyMaster.getByName(strategyName);
        if (strategyDescriptor == null) {
            throw new IllegalArgumentException("strategyName invalid. strategyName: " + strategyName);
        }
        return strategyDescriptor;
    }
}
