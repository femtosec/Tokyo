package jp.co.myogadanimotors.myogadani.eventprocessing.report.cancelack;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public class CancelAck extends BaseEvent<IAsyncEventListener<CancelAck>> {

    private final long requestId;
    private final long orderId;

    public CancelAck(long eventId,
                     long creationTime,
                     long requestId,
                     long orderId,
                     IAsyncEventListener<CancelAck> eventListener) {
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
