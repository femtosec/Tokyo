package jp.co.myogadanimotors.myogadani.eventprocessing.report.cancelreject;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class CancelRejectSender extends BaseEventSender<CancelReject> {

    private long requestId;
    private long orderId;
    private String message;

    public CancelRejectSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected CancelReject createEvent(long eventId, long creationTime, IAsyncEventListener<CancelReject> eventListener) {
        return new CancelReject(
                eventId,
                creationTime,
                requestId,
                orderId,
                message,
                eventListener
        );
    }

    public void sendCancelReject(long requestId, long orderId, String message) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.message = message;
        send();
    }
}
