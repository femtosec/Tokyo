package jp.co.myogadanimotors.myogadani.eventprocessing.report.fill;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.math.BigDecimal;

public class FillSender extends BaseEventSender<FillEvent> {

    private long orderId;
    private BigDecimal execQuantity;

    public FillSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected FillEvent createEvent(long eventId, long creationTime, IAsyncEventListener<FillEvent> eventListener) {
        return new FillEvent(
                eventId,
                creationTime,
                orderId,
                execQuantity,
                eventListener
        );
    }

    public void sendFillEvent(long orderId,
                              BigDecimal execQuantity) {
        this.orderId = orderId;
        this.execQuantity = execQuantity;
        send();
    }
}
