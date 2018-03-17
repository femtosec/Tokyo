package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.event.order.OrderDestination;
import jp.co.myogadanimotors.kohinata.event.order.Orderer;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.BaseStrategyEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;

import static java.util.Objects.requireNonNull;

public abstract class BaseStrategyOrderEvent extends BaseStrategyEvent {

    private final long requestId;
    private final OrderView orderView;
    private final Orderer orderer;
    private final OrderDestination destination;

    public BaseStrategyOrderEvent(long eventId,
                                  long creationTime,
                                  IStrategyEventListener eventListener,
                                  long requestId,
                                  OrderView orderView,
                                  Orderer orderer,
                                  OrderDestination destination) {
        super(eventId, creationTime, eventListener);
        this.requestId = requestId;
        this.orderView = requireNonNull(orderView);
        this.orderer = requireNonNull(orderer);
        this.destination = requireNonNull(destination);
    }

    public long getRequestId() {
        return requestId;
    }

    public OrderView getOrderView() {
        return orderView;
    }

    public Orderer getOrderer() {
        return orderer;
    }

    public OrderDestination getDestination() {
        return destination;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderView: ").append(orderView)
                .append(", orderer: ").append(orderer)
                .append(", destination: ").append(destination);
    }
}
