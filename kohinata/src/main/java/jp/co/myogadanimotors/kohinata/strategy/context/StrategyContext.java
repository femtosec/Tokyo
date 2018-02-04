package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.master.market.IMarket;
import jp.co.myogadanimotors.bunkyo.master.market.MarketState;
import jp.co.myogadanimotors.bunkyo.master.product.IProduct;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.kohinata.event.RequestIdGenerator;
import jp.co.myogadanimotors.kohinata.event.marketdata.IAsyncMarketDataRequestListener;
import jp.co.myogadanimotors.kohinata.event.marketdata.MarketDataRequestSender;
import jp.co.myogadanimotors.kohinata.event.order.IAsyncOrderListener;
import jp.co.myogadanimotors.kohinata.event.report.FillSender;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncFillListener;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncReportListener;
import jp.co.myogadanimotors.kohinata.event.report.ReportSender;
import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerRegistrationListener;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.strategy.StrategyState;

import java.util.Map;

public final class StrategyContext implements IStrategyContext {

    private final RequestIdGenerator requestIdGenerator;
    private final ChildOrderContainer childOrderContainer;
    private final IChildOrderSender childOrderSender;
    private final TimerRegistry timerRegistry;
    private final ReportSender reportSender;
    private final FillSender fillSender;
    private final MarketDataRequestSender marketDataRequestSender;
    private final ITimeSource timeSource;
    private final OrderView orderView;
    private final MarketView marketView;
    private final IProduct product;

    private StrategyState strategyState;
    private IStrategyPendingAmendContext pendingAmendContext;
    private IStrategyPendingAmendProcessor pendingAmendProcessor;
    private IStrategyPendingCancelProcessor pendingCancelProcessor;
    private long currentTime;

    public StrategyContext(EventIdGenerator eventIdGenerator,
                           RequestIdGenerator requestIdGenerator,
                           ITimeSource timeSource,
                           IOrder order,
                           IMarket market,
                           IProduct product,
                           IAsyncOrderListener asyncOrderListener,
                           IAsyncReportListener asyncReportListener,
                           IAsyncFillListener asyncFillListener,
                           IAsyncMarketDataRequestListener asyncMarketDataRequestListener,
                           IAsyncTimerRegistrationListener asyncTimerRegistrationListener) {
        this.requestIdGenerator = requestIdGenerator;
        this.childOrderContainer = new ChildOrderContainer();
        this.childOrderSender = new ChildOrderSender(eventIdGenerator, requestIdGenerator, timeSource, childOrderContainer, order, asyncOrderListener);
        this.timerRegistry = new TimerRegistry(order.getOrderId(), eventIdGenerator, timeSource, asyncTimerRegistrationListener);
        this.reportSender = new ReportSender(eventIdGenerator, timeSource);
        this.fillSender = new FillSender(eventIdGenerator, timeSource);
        this.marketDataRequestSender = new MarketDataRequestSender(eventIdGenerator, timeSource);
        this.timeSource = timeSource;
        this.orderView = new OrderView(order);
        this.marketView = new MarketView(market);
        this.product = product;
        strategyState = StrategyState.PendingNew;
        currentTime = timeSource.getCurrentTime();

        reportSender.addAsyncEventListener(asyncReportListener);
        fillSender.addAsyncEventListener(asyncFillListener);
        marketDataRequestSender.addAsyncEventListener(asyncMarketDataRequestListener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // updaters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void refreshCurrentTime() {
        currentTime = timeSource.getCurrentTime();
    }

    public void onTimer(long userTag, long timerEventTime) {
        timerRegistry.onTimer(userTag, timerEventTime);
    }

    public void updateOrderView(IOrder order) {
        orderView.update(order);
    }

    public void updateExtendedAttributes(Map<String, String> extendedAttributes) {
        orderView.updateExtendedAttributes(extendedAttributes);
    }

    public void addChildOrder(IOrder childOrder) {
        childOrderContainer.addChildOrder(childOrder);
    }

    public void updateChildOrder(IOrder childOrder) {
        childOrderContainer.updateChildOrder(childOrder);
    }

    public void removeChildOrder(IOrder childOrder) {
        childOrderContainer.removeChildOrder(childOrder);
    }

    public void decrementOnTheWireOrdersCount() {
        childOrderContainer.decrementOnTheWireOrdersCount();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // setters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setStrategyState(StrategyState strategyState) {
        this.strategyState = strategyState;
    }

    public void setMarketState(MarketState marketState) {
        marketView.setMarketState(marketState);
    }

    public void setStrategyPendingAmendContext(IStrategyPendingAmendContext pendingAmendContext) {
        this.pendingAmendContext = pendingAmendContext;
    }

    public void setStrategyPendingAmendProcessor(IStrategyPendingAmendProcessor pendingAmendProcessor) {
        this.pendingAmendProcessor = pendingAmendProcessor;
    }

    public void setStrategyPendingCancelProcessor(IStrategyPendingCancelProcessor pendingCancelProcessor) {
        this.pendingCancelProcessor = pendingCancelProcessor;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ReportSender getReportSender() {
        return reportSender;
    }

    public FillSender getFillSender() {
        return fillSender;
    }

    public IStrategyPendingAmendContext getStrategyPendingAmendContext() {
        return pendingAmendContext;
    }

    public IStrategyPendingAmendProcessor getStrategyPendingAmendProcessor() {
        return pendingAmendProcessor;
    }

    public IStrategyPendingCancelProcessor getStrategyPendingCancelProcessor() {
        return pendingCancelProcessor;
    }

    @Override
    public IOrder getOrder() {
        return orderView;
    }

    @Override
    public IMarket getMarket() {
        return marketView;
    }

    @Override
    public IProduct getProduct() {
        return product;
    }

    @Override
    public IChildOrderContainer getChildOrderContainer() {
        return childOrderContainer;
    }

    @Override
    public IChildOrderSender getChildOrderSender() {
        return childOrderSender;
    }

    @Override
    public ITimerRegistry getTimerRegistry() {
        return timerRegistry;
    }

    @Override
    public StrategyState getStrategyState() {
        return strategyState;
    }

    @Override
    public long getCurrentTime() {
        return currentTime;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // utilities
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void subscribeMarketData(String symbol, String mic) {
        marketDataRequestSender.sendMarketDataRequest(
                requestIdGenerator.generateRequestId(),
                product.getId(),
                symbol,
                mic
        );
    }
}
