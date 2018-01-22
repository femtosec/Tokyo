package jp.co.myogadanimotors.myogadani.eventprocessing.report.amendack;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class AmendAckSender extends BaseEventSender<AmendAck> {

    private long requestId;
    private long orderId;

    public AmendAckSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected AmendAck createEvent(long eventId, long creationTime, IAsyncEventListener<AmendAck> eventListener) {
        return new AmendAck(eventId, creationTime, requestId, orderId, eventListener);
    }

    public void sendAmendAck(long requestId, long orderId) {
        this.requestId = requestId;
        this.orderId = orderId;
        send();
    }
}
