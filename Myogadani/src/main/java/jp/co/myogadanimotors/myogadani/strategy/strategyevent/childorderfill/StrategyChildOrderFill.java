package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorderfill;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyChildOrderFill implements IStrategyEvent {

    private final OrderView orderView;
    private final OrderView childOrderView;

    public StrategyChildOrderFill(OrderView orderView, OrderView childOrderView) {
        this.orderView = orderView;
        this.childOrderView = childOrderView;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderFill;
    }

    public final OrderView getOrderView() {
        return orderView;
    }

    public final OrderView getChildOrderView() {
        return childOrderView;
    }
}
