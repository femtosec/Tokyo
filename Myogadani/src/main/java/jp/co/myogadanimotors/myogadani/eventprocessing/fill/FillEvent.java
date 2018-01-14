package jp.co.myogadanimotors.myogadani.eventprocessing.fill;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventType;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;

import java.math.BigDecimal;

public final class FillEvent extends AbstractEvent {

    private final long orderId;
    private final BigDecimal execQuantity;
    private final Orderer orderer;
    private final OrderDestination destination;

    public FillEvent(long eventId,
                     long creationTime,
                     String eventSenderName,
                     long orderId,
                     BigDecimal execQuantity,
                     Orderer orderer,
                     OrderDestination destination) {
        super(eventId, creationTime, eventSenderName);
        this.orderId = orderId;
        this.execQuantity = execQuantity;
        this.orderer = orderer;
        this.destination = destination;
    }

    @Override
    public EventType getEventType() {
        return EventType.Fill;
    }

    public long getOrderId() {
        return orderId;
    }

    public BigDecimal getExecQuantity() {
        return execQuantity;
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
                .append(", orderId: ").append(orderId)
                .append(", orderer: ").append(orderer)
                .append(", destination: ").append(destination);
    }
}
