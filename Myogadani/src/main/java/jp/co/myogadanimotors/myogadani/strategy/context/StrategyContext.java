package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.FillSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.ReportSender;
import jp.co.myogadanimotors.myogadani.idgenerator.IIdGenerator;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketState;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.IProduct;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.StrategyMaster;
import jp.co.myogadanimotors.myogadani.strategy.StrategyState;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public final class StrategyContext implements IStrategyContext {

    private final ChildOrderSender childOrderSender;
    private final ReportSender reportSender;
    private final FillSender fillSender;
    private final ITimeSource timeSource;
    private final OrderView orderView;
    private final MarketView marketView;
    private final IProduct product;

    private StrategyState strategyState;
    private IStrategyPendingAmendContext pendingAmendContext;
    private IStrategyPendingAmendProcessor pendingAmendProcessor;
    private IStrategyPendingCancelProcessor pendingCancelProcessor;
    private long currentTime;

    // todo: fill sender and report sender should be created in this constructor
    public StrategyContext(ReportSender reportSender,
                           FillSender fillSender,
                           EventIdGenerator eventIdGenerator,
                           ITimeSource timeSource,
                           IIdGenerator requestIdGenerator,
                           StrategyMaster strategyMaster,
                           IOrder order,
                           IMarket market,
                           IProduct product) {
        this.childOrderSender = new ChildOrderSender(eventIdGenerator, timeSource, requestIdGenerator, strategyMaster, order);
        this.reportSender = reportSender;
        this.fillSender = fillSender;
        this.timeSource = timeSource;
        this.orderView = new OrderView(order);
        this.marketView = new MarketView(market);
        this.product = product;
        strategyState = StrategyState.PendingNew;
        currentTime = timeSource.getCurrentTime();
    }

    public void refreshCurrentTime() {
        currentTime = timeSource.getCurrentTime();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // setters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setStrategyState(StrategyState strategyState) {
        this.strategyState = strategyState;
    }

    public void setOrder(IOrder order) {
        orderView.update(order);
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
    public ChildOrderSender getChildOrderSender() {
        return childOrderSender;
    }

    @Override
    public StrategyState getStrategyState() {
        return strategyState;
    }

    @Override
    public long getCurrentTime() {
        return currentTime;
    }
}
