package jp.co.myogadanimotors.myogadani.eventprocessing.report.newack;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class NewAckSender extends BaseEventSender<NewAck> {

    private long requestId;
    private long orderId;

    public NewAckSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected NewAck createEvent(long eventId, long creationTime, IAsyncEventListener<NewAck> eventListener) {
        return new NewAck(eventId, creationTime, requestId, orderId, eventListener);
    }

    public void sendNewAck(long requestId, long orderId) {
        this.requestId = requestId;
        this.orderId = orderId;
        send();
    }
}
