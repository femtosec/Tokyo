package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.event.order.OrderDestination;
import jp.co.myogadanimotors.kohinata.event.order.Orderer;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.AbstractStrategyEvent;

import static java.util.Objects.requireNonNull;

public abstract class AbstractStrategyOrderEvent extends AbstractStrategyEvent {

    private final long requestId;
    private final OrderView orderView;
    private final Orderer orderer;
    private final OrderDestination destination;

    public AbstractStrategyOrderEvent(long eventId,
                                      long creationTime,
                                      StrategyContext context,
                                      long requestId,
                                      OrderView orderView,
                                      Orderer orderer,
                                      OrderDestination destination) {
        super(eventId, creationTime, context);
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
