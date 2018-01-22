package jp.co.myogadanimotors.myogadani.eventprocessing.report.amendack;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public class AmendAck extends BaseEvent<IAsyncEventListener<AmendAck>> {

    private final long requestId;
    private final long orderId;

    public AmendAck(long eventId,
                    long creationTime,
                    long requestId,
                    long orderId,
                    IAsyncEventListener<AmendAck> eventListener) {
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
