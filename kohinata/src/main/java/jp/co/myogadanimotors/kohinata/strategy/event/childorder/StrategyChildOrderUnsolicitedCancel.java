package jp.co.myogadanimotors.kohinata.strategy.event.childorder;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyChildOrderUnsolicitedCancel extends BaseStrategyChildOrderReport {

    public StrategyChildOrderUnsolicitedCancel(long eventId,
                                               long creationTime,
                                               IStrategyEventListener strategyEventListener,
                                               OrderView orderView,
                                               OrderView childOrderView,
                                               String childOrderTag,
                                               String message) {
        super(eventId, creationTime, strategyEventListener, orderView, childOrderView, childOrderTag, message);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderUnsolicitedCancel;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyChildOrderUnsolicitedCancel(this);
    }
}
