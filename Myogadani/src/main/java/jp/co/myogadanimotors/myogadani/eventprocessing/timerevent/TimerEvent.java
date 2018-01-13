package jp.co.myogadanimotors.myogadani.eventprocessing.timerevent;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventType;

public final class TimerEvent extends AbstractEvent {

    private final long orderId;
    private final long userTag;
    private final long timerEventTime;

    public TimerEvent(long eventId, long creationTime, String eventSenderName, long orderId, long userTag, long timerEventTime) {
        super(eventId, creationTime, eventSenderName);
        this.orderId = orderId;
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
    }

    @Override
    public EventType getEventType() {
        return EventType.TimerEvent;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getUserTag() {
        return userTag;
    }

    public long getTimerEventTime() {
        return timerEventTime;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderId: ").append(orderId)
                .append(", userTag: ").append(userTag)
                .append(", timerEventTime: ").append(timerEventTime);
    }
}
