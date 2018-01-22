package jp.co.myogadanimotors.myogadani.eventprocessing.report.newrejet;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class NewRejectSender extends BaseEventSender<NewReject> {

    private long requestId;
    private long orderId;
    private String message;

    public NewRejectSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected NewReject createEvent(long eventId, long creationTime, IAsyncEventListener<NewReject> eventListener) {
        return new NewReject(
                eventId,
                creationTime,
                requestId,
                orderId,
                message,
                eventListener
        );
    }

    public void sendNewReject(long requestId, long orderId, String message) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.message = message;
        send();
    }
}
