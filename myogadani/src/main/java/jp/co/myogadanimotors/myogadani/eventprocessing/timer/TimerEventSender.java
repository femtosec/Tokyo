package jp.co.myogadanimotors.myogadani.eventprocessing.timer;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEvent;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class TimerEventSender extends BaseEventSender<IAsyncTimerEventListener> {

    private long orderId;
    private long timerTag;
    private long timerEventTime;

    public TimerEventSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendTimerEvent(long orderId, long timerTag, long timerEventTime) {
        this.orderId = orderId;
        this.timerTag = timerTag;
        this.timerEventTime = timerEventTime;
        send(this::createTimerEvent);
    }

    private IEvent createTimerEvent(long eventId, long creationTime, IAsyncTimerEventListener asyncEventListener) {
        return new TimerEvent(eventId, creationTime, orderId, timerTag, timerEventTime,asyncEventListener);
    }
}
