package jp.co.myogadanimotors.kohinata.strategy.event.childorder;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyChildOrderNewAck extends BaseStrategyChildOrderReport {

    public StrategyChildOrderNewAck(long eventId,
                                    long creationTime,
                                    IStrategyEventListener strategyEventListener,
                                    OrderView orderView,
                                    OrderView childOrderView,
                                    String childOrderTag) {
        super(eventId, creationTime, strategyEventListener, orderView, childOrderView, childOrderTag, null);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderNewAck;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyChildOrderNewAck(this);
    }
}
