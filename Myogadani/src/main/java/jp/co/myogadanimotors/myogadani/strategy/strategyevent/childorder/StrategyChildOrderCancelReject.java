package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyChildOrderCancelReject extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderCancelReject(long eventId,
                                          long creationTime,
                                          IStrategy strategy,
                                          OrderView orderView,
                                          OrderView childOrderView,
                                          String childOrderTag,
                                          String message) {
        super(eventId, creationTime, strategy, orderView, childOrderView, childOrderTag, message);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyChildOrderCancelReject(this);
    }
}
