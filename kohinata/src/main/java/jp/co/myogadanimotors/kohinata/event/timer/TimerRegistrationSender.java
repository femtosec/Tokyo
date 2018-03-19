package jp.co.myogadanimotors.kohinata.event.timer;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

public class TimerRegistrationSender extends BaseEventSender<IAsyncTimerRegistrationListener> {

    public TimerRegistrationSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendTimerRegistration(long orderId, long timerTag, long timerEventTime) {
        send((eventId, creationTime, asyncEventListener) ->
                new TimerRegistration(
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
