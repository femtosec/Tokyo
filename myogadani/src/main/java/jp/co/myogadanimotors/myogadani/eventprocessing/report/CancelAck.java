package jp.co.myogadanimotors.myogadani.eventprocessing.report;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;

public final class CancelAck extends BaseEvent<IAsyncReportListener> {

    private final long requestId;
    private final long orderId;

    public CancelAck(long eventId,
                     long creationTime,
                     long requestId,
                     long orderId,
                     IAsyncReportListener eventListener) {
        super(eventId, creationTime, eventListener);
        this.requestId = requestId;
        this.orderId = orderId;
    }

    public long getRequestId() {
        return requestId;
    }

    public long getOrderId() {
        return orderId;
    }

    @Override
    protected void callEventListener(IAsyncReportListener eventListener) {
        eventListener.processCancelAck(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId);
    }
}
