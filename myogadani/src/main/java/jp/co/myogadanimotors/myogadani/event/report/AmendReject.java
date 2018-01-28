package jp.co.myogadanimotors.myogadani.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

public final class AmendReject extends BaseEvent<IAsyncReportListener> {

    private final long requestId;
    private final long orderId;
    private final String message;

    public AmendReject(long eventId,
                       long creationTime,
                       long requestId,
                       long orderId,
                       String message,
                       IAsyncReportListener eventListener) {
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
    protected void callEventListener(IAsyncReportListener eventListener) {
        eventListener.processAmendReject(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId)
                .append(", message: ").append(message);
    }
}
