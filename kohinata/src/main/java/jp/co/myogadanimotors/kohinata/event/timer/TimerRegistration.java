package jp.co.myogadanimotors.kohinata.event.timer;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

public final class TimerRegistration extends BaseEvent<IAsyncTimerRegistrationListener> {

    private final long orderId;
    private final long timerTag;
    private final long timerEventTime;

    public TimerRegistration(long eventId,
                             long creationTime,
                             IAsyncTimerRegistrationListener eventListener,
                             long orderId,
                             long timerTag,
                             long timerEventTime) {
        super(eventId, creationTime, eventListener);
        this.orderId = orderId;
        this.timerTag = timerTag;
        this.timerEventTime = timerEventTime;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getTimerTag() {
        return timerTag;
    }

    public long getTimerEventTime() {
        return timerEventTime;
    }

    @Override
    protected void callEventListener(IAsyncTimerRegistrationListener eventListener) {
        eventListener.processTimerRegistration(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderId: ").append(orderId)
                .append(", timerTag: ").append(timerTag)
                .append(", timerEventTime: ").append(timerEventTime);
    }
}
