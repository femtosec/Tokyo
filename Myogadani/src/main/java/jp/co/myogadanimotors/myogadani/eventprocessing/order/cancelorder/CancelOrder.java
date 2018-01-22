package jp.co.myogadanimotors.myogadani.eventprocessing.order.cancelorder;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public final class CancelOrder extends BaseEvent<IAsyncEventListener<CancelOrder>> {

    private final long requestId;
    private final long orderId;

    public CancelOrder(long eventId,
                       long creationTime,
                       long requestId,
                       long orderId,
                       IAsyncEventListener<CancelOrder> eventListener) {
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
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId);
    }
}
