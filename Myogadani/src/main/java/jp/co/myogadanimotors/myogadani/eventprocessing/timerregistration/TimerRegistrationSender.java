package jp.co.myogadanimotors.myogadani.eventprocessing.timerregistration;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventStream;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class TimerRegistrationSender extends AbstractEventSender<TimerRegistration> {

    private long orderId;
    private long userTag;
    private long timerEventTime;

    public TimerRegistrationSender(String eventSenderName, IEventIdGenerator idGenerator, ITimeSource timeSource, IEventStream... eventStreams) {
        super(eventSenderName, idGenerator, timeSource, eventStreams);
    }

    @Override
    protected TimerRegistration createEvent() {
        return new TimerRegistration(
                generateEventId(),
                getCurrentTime(),
                getEventSenderName(),
                orderId,
                userTag,
                timerEventTime
        );
    }

    public void registerTimer(long orderId, long userTag, long timerEventTime) {
        this.orderId = orderId;
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
        send();
    }
}
