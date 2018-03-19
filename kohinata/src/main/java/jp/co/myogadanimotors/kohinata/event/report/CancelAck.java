package jp.co.myogadanimotors.kohinata.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

public final class CancelAck extends BaseEvent<IAsyncReportListener> {

    private final long requestId;
    private final long orderId;

    public CancelAck(long eventId,
                     long creationTime,
                     IAsyncReportListener eventListener,
                     long requestId,
                     long orderId) {
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
