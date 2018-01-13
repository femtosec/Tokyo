package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;

public abstract class AbstractStrategyReportEvent implements IStrategyEvent {

    private final OrderView orderView;

    public AbstractStrategyReportEvent(OrderView orderView) {
        this.orderView = orderView;
    }

    public OrderView getOrderView() {
        return orderView;
    }
}
