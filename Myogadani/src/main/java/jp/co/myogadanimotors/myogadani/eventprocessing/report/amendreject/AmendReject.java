package jp.co.myogadanimotors.myogadani.eventprocessing.report.amendreject;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public class AmendReject extends BaseEvent<IAsyncEventListener<AmendReject>> {

    private final long requestId;
    private final long orderId;
    private final String message;

    public AmendReject(long eventId,
                       long creationTime,
                       long requestId,
                       long orderId,
                       String message,
                       IAsyncEventListener<AmendReject> eventListener) {
        super(eventId, creationTime, eventListener);
        this.requestId = requestId;
        this.orderId = orderId;
        this.message = message;
    }

    public long getRequestId() {
        return requestId;
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
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId)
                .append(", message: ").append(message);
    }
}
