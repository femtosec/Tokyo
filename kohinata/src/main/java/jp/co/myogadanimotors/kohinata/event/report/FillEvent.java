package jp.co.myogadanimotors.kohinata.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

import java.math.BigDecimal;

public final class FillEvent extends BaseEvent<IAsyncFillListener> {

    private final long orderId;
    private final BigDecimal execQuantity;

    public FillEvent(long eventId,
                     long creationTime,
                     IAsyncFillListener fillEventListener,
                     long orderId,
                     BigDecimal execQuantity) {
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
    protected void callEventListener(IAsyncFillListener eventListener) {
        eventListener.processFillEvent(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderId: ").append(orderId)
                .append(", execQuantity: ").append(execQuantity);
    }
}
