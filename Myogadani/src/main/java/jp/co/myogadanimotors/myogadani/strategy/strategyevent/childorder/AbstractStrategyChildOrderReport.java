package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;

public abstract class AbstractStrategyChildOrderReport implements IStrategyEvent {

    private final OrderView orderView;
    private final OrderView childOrderView;
    private final String message;

    public AbstractStrategyChildOrderReport(OrderView orderView, OrderView childOrderView, String message) {
        this.orderView = orderView;
        this.childOrderView = childOrderView;
        this.message = message;
    }

    public final OrderView getOrderView() {
        return orderView;
    }

    public final OrderView getChildOrderView() {
        return childOrderView;
    }

    public final String getMessage() {
        return message;
    }
}
