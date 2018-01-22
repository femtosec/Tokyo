package jp.co.myogadanimotors.myogadani.eventprocessing.order;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;

public final class CancelOrder extends BaseEvent<IAsyncOrderListener> {

    private final long requestId;
    private final long orderId;

    public CancelOrder(long eventId,
                       long creationTime,
                       long requestId,
                       long orderId,
                       IAsyncOrderListener eventListener) {
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
    protected void callEventListener(IAsyncOrderListener eventListener) {
        eventListener.processCancelOrder(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId);
    }
}
