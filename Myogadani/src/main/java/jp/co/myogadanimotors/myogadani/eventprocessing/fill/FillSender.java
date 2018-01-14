package jp.co.myogadanimotors.myogadani.eventprocessing.fill;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventStream;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.math.BigDecimal;

public class FillSender extends AbstractEventSender<FillEvent> {

    private long orderId;
    private BigDecimal execQuantity;
    private Orderer orderer;
    private OrderDestination destination;

    public FillSender(String eventSenderName, IEventIdGenerator idGenerator, ITimeSource timeSource, IEventStream... eventStreams) {
        super(eventSenderName, idGenerator, timeSource, eventStreams);
    }

    @Override
    protected FillEvent createEvent() {
        return new FillEvent(
                generateEventId(),
                getCurrentTime(),
                getEventSenderName(),
                orderId,
                execQuantity,
                orderer,
                destination
        );
    }

    public void sendFill(long orderId, BigDecimal execQuantity, Orderer orderer, OrderDestination destination) {
        this.orderId = orderId;
        this.execQuantity = execQuantity;
        this.orderer = orderer;
        this.destination = destination;
        send();
    }
}
