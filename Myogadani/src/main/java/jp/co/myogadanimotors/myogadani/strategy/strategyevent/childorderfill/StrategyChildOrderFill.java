package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorderfill;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyChildOrderFill implements IStrategyEvent {

    private final OrderView orderView;

    public StrategyChildOrderFill(OrderView orderView) {
        this.orderView = orderView;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderFill;
    }

    public final OrderView getOrderView() {
        return orderView;
    }
}
