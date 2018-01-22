package jp.co.myogadanimotors.myogadani.eventprocessing.report;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;

public class AmendAck extends BaseEvent<IAsyncReportListener> {

    private final long requestId;
    private final long orderId;

    public AmendAck(long eventId,
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
        eventListener.processAmendAck(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId);
    }
}
