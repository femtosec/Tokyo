package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyChildOrderNewAck extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderNewAck(OrderView orderView, OrderView childOrderView, String message) {
        super(orderView, childOrderView, message);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderNewAck;
    }
}
