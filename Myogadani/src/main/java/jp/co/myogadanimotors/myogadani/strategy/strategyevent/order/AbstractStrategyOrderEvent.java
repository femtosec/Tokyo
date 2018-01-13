package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;

public abstract class AbstractStrategyOrderEvent implements IStrategyEvent {

    private final long requestId;
    private final OrderView orderView;
    private final Orderer orderer;
    private final OrderDestination destination;

    public AbstractStrategyOrderEvent(long requestId, OrderView orderView, Orderer orderer, OrderDestination destination) {
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
}
