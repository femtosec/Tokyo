package jp.co.myogadanimotors.myogadani.eventprocessing.timer.timerregstration;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class TimerRegistrationSender extends BaseEventSender<TimerRegistration> {

    private long orderId;
    private long userTag;
    private long timerEventTime;

    public TimerRegistrationSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected TimerRegistration createEvent(long eventId, long creationTime, IAsyncEventListener<TimerRegistration> eventListener) {
        return new TimerRegistration(eventId, creationTime, orderId, userTag, timerEventTime, eventListener);
    }

    public void sendTimerRegistration(long orderId, long userTag, long timerEventTime) {
        this.orderId = orderId;
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
        send();
    }
}
