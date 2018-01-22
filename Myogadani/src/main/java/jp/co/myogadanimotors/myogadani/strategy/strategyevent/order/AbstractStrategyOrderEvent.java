package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.eventprocessing.order.neworder.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.neworder.Orderer;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.AbstractStrategyEvent;

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
        this.orderView = orderView;
        this.orderer = orderer;
        this.destination = destination;
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
