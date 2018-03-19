package jp.co.myogadanimotors.kohinata.event.timer;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

public class TimerEventSender extends BaseEventSender<IAsyncTimerEventListener> {

    public TimerEventSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendTimerEvent(long orderId, long timerTag, long timerEventTime) {
        send((eventId, creationTime, asyncEventListener) ->
                new TimerEvent(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderId,
                        timerTag,
                        timerEventTime
                )
        );
    }
}
