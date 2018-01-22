package jp.co.myogadanimotors.myogadani.eventprocessing.report;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;

public class UnsolicitedCancel extends BaseEvent<IAsyncReportListener> {

    private final long orderId;
    private final String message;

    public UnsolicitedCancel(long eventId,
                             long creationTime,
                             long orderId,
                             String message,
                             IAsyncReportListener eventListener) {
        super(eventId, creationTime, eventListener);
        this.orderId = orderId;
        this.message = message;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected void callEventListener(IAsyncReportListener eventListener) {
        eventListener.processUnsolicitedCancel(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderId: ").append(orderId)
                .append(", message: ").append(message);
    }
}
