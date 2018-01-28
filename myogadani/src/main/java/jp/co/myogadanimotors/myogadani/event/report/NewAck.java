package jp.co.myogadanimotors.myogadani.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

public final class NewAck extends BaseEvent<IAsyncReportListener> {

    private final long requestId;
    private final long orderId;

    public NewAck(long eventId,
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
        eventListener.processNewAck(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId);
    }
}
