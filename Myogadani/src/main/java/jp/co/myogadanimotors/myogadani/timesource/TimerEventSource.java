package jp.co.myogadanimotors.myogadani.timesource;

import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.IAsyncTimerEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.TimerEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.TimerRegistration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import static java.lang.Thread.sleep;

public class TimerEventSource implements ITimerEventSource, Runnable  {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final ITimeSource timeSource;
    private final long timerEventResolution;
    private final Executor eventQueue;
    private final TimerEventSender timerEventSender;
    private final List<TimerRegistration> timerRegistrations = Collections.synchronizedList(new ArrayList<>());

    private boolean isRunning = false;

    public TimerEventSource(EventIdGenerator eventIdGenerator, ITimeSource timeSource, long timerEventResolution, Executor eventQueue) {
        this.timeSource = timeSource;
        this.eventQueue = eventQueue;
        this.timerEventResolution = timerEventResolution;
        this.timerEventSender = new TimerEventSender(eventIdGenerator, timeSource);
    }

    @Override
    public void addEventListener(IAsyncTimerEventListener asyncTimerEventListener) {
        timerEventSender.addAsyncEventListener(asyncTimerEventListener);
    }

    @Override
    public void processTimerRegistration(TimerRegistration timerRegistration) {
        logger.info("registering timer. (timerRegistration: {})", timerRegistration);
        timerRegistrations.add(timerRegistration);
    }

    @Override
    public Executor getEventQueue() {
        return eventQueue;
    }

    @Override
    public void run() {
        logger.info("starting timer event loop. (currentTime: {})", timeSource.getCurrentTime());

        while (isRunning) {
            try {
                sleep(timerEventResolution);
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(), e);
            }

            if (timerRegistrations.size() == 0) continue;

            TimerRegistration[] timerRegistrationsCopy = timerRegistrations.toArray(new TimerRegistration[timerRegistrations.size()]);
            long currentTime = timeSource.getCurrentTime();

            for (TimerRegistration timerRegistration : timerRegistrationsCopy) {
                if (raiseTimerEventIfNecessary(timerRegistration, currentTime)) {
                    timerRegistrations.remove(timerRegistration);
                }
            }
        }

        logger.info("shutdown succeeded.");
    }

    private boolean raiseTimerEventIfNecessary(TimerRegistration timerRegistration, long currentTime) {
        if (currentTime < timerRegistration.getTimerEventTime()) return false;

        if (logger.getLevel() == Level.TRACE) {
            logger.trace("raising timer event. (timerRegistration {})", timerRegistration);
        }

        timerEventSender.sendTimerEvent(timerRegistration.getOrderId(), timerRegistration.getTimerTag(), timerRegistration.getTimerEventTime());

        return true;
    }

    public void terminate() {
        logger.info("shutting down the event loop.");
        isRunning = false;
    }
}
