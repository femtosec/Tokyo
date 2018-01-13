package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;

public abstract class AbstractStrategyChildOrderReport implements IStrategyEvent {

    private final OrderView orderView;
    private final String message;

    public AbstractStrategyChildOrderReport(OrderView orderView, String message) {
        this.orderView = orderView;
        this.message = message;
    }

    public final OrderView getOrderView() {
        return orderView;
    }

    public final String getMessage() {
        return message;
    }
}
