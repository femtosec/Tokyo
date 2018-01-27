package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.RequestIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.marketdata.IAsyncMarketDataRequestListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.IAsyncOrderListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncFillListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncReportListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.IAsyncTimerRegistrationListener;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.master.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.master.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.master.product.IProduct;
import jp.co.myogadanimotors.myogadani.store.master.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class StrategyContextFactory {

    private final EventIdGenerator eventIdGenerator;
    private final RequestIdGenerator requestIdGenerator;
    private final ITimeSource timeSource;
    private final MarketMaster marketMaster;
    private final ProductMaster productMaster;
    private IAsyncOrderListener asyncOrderListener;
    private IAsyncReportListener asyncReportListener;
    private IAsyncFillListener asyncFillListener;
    private IAsyncMarketDataRequestListener asyncMarketDataRequestListener;
    private IAsyncTimerRegistrationListener asyncTimerRegistrationListener;

    public StrategyContextFactory(EventIdGenerator eventIdGenerator,
                                  RequestIdGenerator requestIdGenerator,
                                  ITimeSource timeSource,
                                  MarketMaster marketMaster,
                                  ProductMaster productMaster) {
        this.eventIdGenerator = eventIdGenerator;
        this.requestIdGenerator = requestIdGenerator;
        this.timeSource = timeSource;
        this.marketMaster = marketMaster;
        this.productMaster = productMaster;
    }

    public void addEventListeners(IAsyncOrderListener asyncOrderListener,
                                  IAsyncReportListener asyncReportListener,
                                  IAsyncFillListener asyncFillListener,
                                  IAsyncMarketDataRequestListener asyncMarketDataRequestListener,
                                  IAsyncTimerRegistrationListener asyncTimerRegistrationListener) {
        this.asyncOrderListener = asyncOrderListener;
        this.asyncReportListener = asyncReportListener;
        this.asyncFillListener = asyncFillListener;
        this.asyncMarketDataRequestListener = asyncMarketDataRequestListener;
        this.asyncTimerRegistrationListener = asyncTimerRegistrationListener;
    }

    public StrategyContext create(IOrder order) {
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
}
