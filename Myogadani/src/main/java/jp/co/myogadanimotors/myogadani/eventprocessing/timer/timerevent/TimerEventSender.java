package jp.co.myogadanimotors.myogadani.eventprocessing.timer.timerevent;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class TimerEventSender extends BaseEventSender<TimerEvent> {

    private long orderId;
    private long userTag;
    private long timerEventTime;

    public TimerEventSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected TimerEvent createEvent(long eventId, long creationTime, IAsyncEventListener<TimerEvent> eventListener) {
        return new TimerEvent(eventId, creationTime, orderId, userTag, timerEventTime,eventListener);
    }

    public void sendTimerEvent(long orderId, long userTag, long timerEventTime) {
        this.orderId = orderId;
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
        send();
    }
}
