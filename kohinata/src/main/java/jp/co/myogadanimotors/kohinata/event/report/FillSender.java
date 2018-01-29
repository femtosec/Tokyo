package jp.co.myogadanimotors.kohinata.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

import java.math.BigDecimal;

public class FillSender extends BaseEventSender<IAsyncFillListener> {

    private long orderId;
    private BigDecimal execQuantity;

    public FillSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendFill(long orderId, BigDecimal execQuantity) {
        this.orderId = orderId;
        this.execQuantity = execQuantity;
        send(this::createFill);
    }

    private IEvent createFill(long eventId, long creationTime, IAsyncFillListener asyncEventListener) {
        return new FillEvent(
                eventId,
                creationTime,
                orderId,
                execQuantity,
                asyncEventListener
        );
    }
}
