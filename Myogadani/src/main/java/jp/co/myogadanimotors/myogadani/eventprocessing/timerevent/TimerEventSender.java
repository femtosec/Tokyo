package jp.co.myogadanimotors.myogadani.eventprocessing.timerevent;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventStream;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class TimerEventSender extends AbstractEventSender<TimerEvent> {

    private long orderId;
    private long userTag;
    private long timerEventTime;

    public TimerEventSender(String eventSenderName,
                            IEventIdGenerator idGenerator,
                            ITimeSource timeSource,
                            IEventStream... eventStreams) {
        super(eventSenderName, idGenerator, timeSource, eventStreams);
    }

    @Override
    protected TimerEvent createEvent() {
        return new TimerEvent(
                generateEventId(),
                getCurrentTime(),
                getEventSenderName(),
                orderId,
                userTag,
                timerEventTime
        );
    }

    public void sendTimerEvent(long orderId, long userTag, long timerEventTime) {
        this.orderId = orderId;
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
        send();
    }
}
