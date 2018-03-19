package jp.co.myogadanimotors.kohinata.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

public final class NewAck extends BaseEvent<IAsyncReportListener> {

    private final long requestId;
    private final long orderId;

    public NewAck(long eventId,
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
        eventListener.processNewAck(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId);
    }
}
