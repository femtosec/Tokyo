package jp.co.myogadanimotors.myogadani.eventprocessing.timerregistration;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventType;

public class TimerRegistration extends AbstractEvent {

    private final long orderId;
    private final long userTag;
    private final long timerEventTime;

    public TimerRegistration(long eventId,
                             long creationTime,
                             String eventSenderName,
                             long orderId,
                             long userTag,
                             long timerEventTime) {
        super(eventId, creationTime, eventSenderName);
        this.orderId = orderId;
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
    }

    @Override
    public EventType getEventType() {
        return EventType.TimerRegistration;
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
}
