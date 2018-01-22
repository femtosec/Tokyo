package jp.co.myogadanimotors.myogadani.eventprocessing.report.unsolicitedcancel;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public class UnsolicitedCancel extends BaseEvent<IAsyncEventListener<UnsolicitedCancel>> {

    private final long orderId;
    private final String message;

    public UnsolicitedCancel(long eventId,
                             long creationTime,
                             long orderId,
                             String message,
                             IAsyncEventListener<UnsolicitedCancel> eventListener) {
        super(eventId, creationTime, eventListener);
        this.orderId = orderId;
        this.message = message;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderId: ").append(orderId)
                .append(", message: ").append(message);
    }
}
