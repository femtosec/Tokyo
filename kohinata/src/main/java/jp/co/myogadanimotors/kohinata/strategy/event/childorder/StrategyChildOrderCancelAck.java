package jp.co.myogadanimotors.kohinata.strategy.event.childorder;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyChildOrderCancelAck extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderCancelAck(long eventId,
                                       long creationTime,
                                       StrategyContext context,
                                       OrderView orderView,
                                       OrderView childOrderView,
                                       String childOrderTag) {
        super(eventId, creationTime, context, orderView, childOrderView, childOrderTag, null);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderCancelAck;
    }

    @Override
    protected void callEventProcessor(StrategyContext context) {
        context.processStrategyChildOrderCancelAck(this);
    }
}
