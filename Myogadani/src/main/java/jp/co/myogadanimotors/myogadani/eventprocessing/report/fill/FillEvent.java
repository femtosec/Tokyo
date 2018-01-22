package jp.co.myogadanimotors.myogadani.eventprocessing.report.fill;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

import java.math.BigDecimal;

public final class FillEvent extends BaseEvent<IAsyncEventListener<FillEvent>> {

    private final long orderId;
    private final BigDecimal execQuantity;

    public FillEvent(long eventId,
                     long creationTime,
                     long orderId,
                     BigDecimal execQuantity,
                     IAsyncEventListener<FillEvent> fillEventListener) {
        super(eventId, creationTime, fillEventListener);
        this.orderId = orderId;
        this.execQuantity = execQuantity;
    }

    public long getOrderId() {
        return orderId;
    }

    public BigDecimal getExecQuantity() {
        return execQuantity;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderId: ").append(orderId)
                .append(", execQuantity: ").append(execQuantity);
    }
}
