package jp.co.myogadanimotors.myogadani.eventprocessing.report.cancelack;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class CancelAckSender extends BaseEventSender<CancelAck> {

    private long requestId;
    private long orderId;

    public CancelAckSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected CancelAck createEvent(long eventId, long creationTime, IAsyncEventListener<CancelAck> eventListener) {
        return new CancelAck(eventId, creationTime, requestId, orderId, eventListener);
    }

    public void sendCancelAck(long requestId, long orderId) {
        this.requestId = requestId;
        this.orderId = orderId;
        send();
    }
}
