package jp.co.myogadanimotors.myogadani.eventprocessing.timer.timerevent;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public class TimerEvent extends BaseEvent<IAsyncEventListener<TimerEvent>> {

    private final long orderId;
    private final long userTag;
    private final long timerEventTime;

    public TimerEvent(long eventId,
                      long creationTime,
                      long orderId,
                      long userTag,
                      long timerEventTime,
                      IAsyncEventListener<TimerEvent> eventListener) {
        super(eventId, creationTime, eventListener);
        this.orderId = orderId;
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
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
