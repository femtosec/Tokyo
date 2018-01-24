package jp.co.myogadanimotors.myogadani.eventprocessing.timer;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEvent;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class TimerRegistrationSender extends BaseEventSender<IAsyncTimerRegistrationListener> {

    private long orderId;
    private long timerTag;
    private long timerEventTime;

    public TimerRegistrationSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendTimerRegistration(long orderId, long timerTag, long timerEventTime) {
        this.orderId = orderId;
        this.timerTag = timerTag;
        this.timerEventTime = timerEventTime;
        send(this::createTimerRegistration);
    }

    private IEvent createTimerRegistration(long eventId, long creationTime, IAsyncTimerRegistrationListener asyncEventListener) {
        return new TimerRegistration(eventId, creationTime, orderId, timerTag, timerEventTime, asyncEventListener);
    }
}
