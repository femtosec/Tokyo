package jp.co.myogadanimotors.myogadani.eventprocessing.report.amendreject;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class AmendRejectSender extends BaseEventSender<AmendReject> {

    private long requestId;
    private long orderId;
    private String message;

    public AmendRejectSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected AmendReject createEvent(long eventId, long creationTime, IAsyncEventListener<AmendReject> eventListener) {
        return new AmendReject(
                eventId,
                creationTime,
                requestId,
                orderId,
                message,
                eventListener
        );
    }

    public void sendAmendReject(long requestId, long orderId, String message) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.message = message;
        send();
    }
}
