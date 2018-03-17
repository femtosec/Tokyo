package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.BaseStrategyEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;

import static java.util.Objects.requireNonNull;

public abstract class BaseStrategyReportEvent extends BaseStrategyEvent {

    private final OrderView orderView;

    public BaseStrategyReportEvent(long eventId,
                                   long creationTime,
                                   IStrategyEventListener eventListener,
                                   OrderView orderView) {
        super(eventId, creationTime, eventListener);
        this.orderView = requireNonNull(orderView);
    }

    public OrderView getOrderView() {
        return orderView;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderView: ").append(orderView);
    }
}
