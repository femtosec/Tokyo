package jp.co.myogadanimotors.myogadani.eventprocessing.report.unsolicitedcancel;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class UnsolicitedCancelSender extends BaseEventSender<UnsolicitedCancel> {

    private long orderId;
    private String message;

    public UnsolicitedCancelSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected UnsolicitedCancel createEvent(long eventId, long creationTime, IAsyncEventListener<UnsolicitedCancel> asyncEventListener) {
        return new UnsolicitedCancel(eventId, creationTime, orderId, message, asyncEventListener);
    }

    public void sendUnsolicitedCancel(long orderId, String message) {
        this.orderId = orderId;
        this.message = message;
        send();
    }
}
