package jp.co.myogadanimotors.kohinata.strategy.event.childorder;

import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;

public final class StrategyChildOrderAmendReject extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderAmendReject(long eventId,
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
        strategy.processStrategyChildOrderAmendReject(this);
    }
}
