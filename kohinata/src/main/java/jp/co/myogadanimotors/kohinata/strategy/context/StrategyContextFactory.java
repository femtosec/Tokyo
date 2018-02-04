package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.master.market.IMarket;
import jp.co.myogadanimotors.bunkyo.master.market.MarketMaster;
import jp.co.myogadanimotors.bunkyo.master.product.IProduct;
import jp.co.myogadanimotors.bunkyo.master.product.ProductMaster;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.kohinata.event.RequestIdGenerator;
import jp.co.myogadanimotors.kohinata.event.marketdata.IAsyncMarketDataRequestListener;
import jp.co.myogadanimotors.kohinata.event.order.IAsyncOrderListener;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncFillListener;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncReportListener;
import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerRegistrationListener;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;

import static java.util.Objects.requireNonNull;

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
        this.eventIdGenerator = requireNonNull(eventIdGenerator);
        this.requestIdGenerator = requireNonNull(requestIdGenerator);
        this.timeSource = requireNonNull(timeSource);
        this.marketMaster = requireNonNull(marketMaster);
        this.productMaster = requireNonNull(productMaster);
    }

    public void addEventListeners(IAsyncOrderListener asyncOrderListener,
                                  IAsyncReportListener asyncReportListener,
                                  IAsyncFillListener asyncFillListener,
                                  IAsyncMarketDataRequestListener asyncMarketDataRequestListener,
                                  IAsyncTimerRegistrationListener asyncTimerRegistrationListener) {
        this.asyncOrderListener = requireNonNull(asyncOrderListener);
        this.asyncReportListener = requireNonNull(asyncReportListener);
        this.asyncFillListener = requireNonNull(asyncFillListener);
        this.asyncMarketDataRequestListener = requireNonNull(asyncMarketDataRequestListener);
        this.asyncTimerRegistrationListener = requireNonNull(asyncTimerRegistrationListener);
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
