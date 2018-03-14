package jp.co.myogadanimotors.kohinata.strategy.event.childorder;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyChildOrderUnsolicitedCancel extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderUnsolicitedCancel(long eventId,
                                               long creationTime,
                                               StrategyContext context,
                                               OrderView orderView,
                                               OrderView childOrderView,
                                               String childOrderTag,
                                               String message) {
        super(eventId, creationTime, context, orderView, childOrderView, childOrderTag, message);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderUnsolicitedCancel;
    }

    @Override
    protected void callEventProcessor(StrategyContext context) {
        context.processStrategyChildOrderUnsolicitedCancel(this);
    }
}
