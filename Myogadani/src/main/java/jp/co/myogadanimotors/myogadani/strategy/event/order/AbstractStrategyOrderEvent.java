package jp.co.myogadanimotors.myogadani.strategy.event.order;

import jp.co.myogadanimotors.myogadani.eventprocessing.order.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.Orderer;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.event.AbstractStrategyEvent;

import static jp.co.myogadanimotors.myogadani.common.Utility.notNull;

public abstract class AbstractStrategyOrderEvent extends AbstractStrategyEvent {

    private final long requestId;
    private final OrderView orderView;
    private final Orderer orderer;
    private final OrderDestination destination;

    public AbstractStrategyOrderEvent(long eventId,
                                      long creationTime,
                                      IStrategy strategy,
                                      long requestId,
                                      OrderView orderView,
                                      Orderer orderer,
                                      OrderDestination destination) {
        super(eventId, creationTime, strategy);
        this.requestId = requestId;
        this.orderView = notNull(orderView);
        this.orderer = notNull(orderer);
        this.destination = notNull(destination);
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
