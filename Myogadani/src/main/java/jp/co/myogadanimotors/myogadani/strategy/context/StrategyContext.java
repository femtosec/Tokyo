package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.eventprocessing.report.ReportSender;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketState;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.IProduct;
import jp.co.myogadanimotors.myogadani.strategy.StrategyState;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public final class StrategyContext implements IStrategyContext {

    private final ReportSender reportSender;
    private final ITimeSource timeSource;
    private final OrderView orderView;
    private final MarketView marketView;
    private final IProduct product;

    private StrategyState strategyState;
    private IStrategyPendingAmendProcessor pendingAmendProcessor;
    private IStrategyPendingCancelProcessor pendingCancelProcessor;
    private long currentTime;

    public StrategyContext(ReportSender reportSender,
                           ITimeSource timeSource,
                           IOrder order,
                           IMarket market,
                           IProduct product) {
        this.reportSender = reportSender;
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
    public IStrategyPendingAmendProcessor getStrategyPendingAmendProcessor() {
        return pendingAmendProcessor;
    }

    @Override
    public IStrategyPendingCancelProcessor getStrategyPendingCancelProcessor() {
        return pendingCancelProcessor;
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
