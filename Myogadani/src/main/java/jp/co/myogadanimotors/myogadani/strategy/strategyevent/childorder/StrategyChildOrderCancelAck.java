package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyChildOrderCancelAck extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderCancelAck(OrderView orderView, String message) {
        super(orderView, message);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderCancelAck;
    }
}
