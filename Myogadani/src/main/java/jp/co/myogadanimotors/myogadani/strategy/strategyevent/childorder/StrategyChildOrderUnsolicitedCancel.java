package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public class StrategyChildOrderUnsolicitedCancel extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderUnsolicitedCancel(OrderView orderView, String message) {
        super(orderView, message);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderUnsolicitedCancel;
    }
}
