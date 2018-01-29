package jp.co.myogadanimotors.kohinata.timereventsource;

import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerEventListener;
import jp.co.myogadanimotors.kohinata.event.timer.TimerEventSender;
import jp.co.myogadanimotors.kohinata.event.timer.TimerRegistration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class TimerEventSource implements ITimerEventSource  {

    private final Logger logger = LogManager.getLogger(getClass().getName());
    
    private final int RUNNING = 0;
    private final int TERMINATED = 1;
    private final AtomicInteger state = new AtomicInteger(RUNNING);

    private final ITimeSource timeSource;
    private final long timerEventResolution;
    private final Executor eventQueue;
    private final TimerEventSender timerEventSender;
    private final List<TimerRegistration> timerRegistrations = Collections.synchronizedList(new ArrayList<>());

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
    public void shutdown() {
        int TERMINATING = 2;
        state.set(TERMINATING);
        logger.info("terminating.");
    }

    @Override
    public boolean awaitTermination(long timeOut) throws InterruptedException {
        if (state.get() == TERMINATED) {
            return true;
        }

        if (timeOut > 0) {
            logger.info("awaiting termination. (timeOut: {})", timeOut);
            sleep(timeOut);

            return awaitTermination(0);
        }

        return false;
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
        state.set(RUNNING);
        
        logger.info("starting timer event loop. (currentTime: {})", timeSource.getCurrentTime());

        while (state.get() == RUNNING) {
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
        
        state.set(TERMINATED);
    }

    private boolean raiseTimerEventIfNecessary(TimerRegistration timerRegistration, long currentTime) {
        if (currentTime < timerRegistration.getTimerEventTime()) return false;

        if (logger.getLevel() == Level.TRACE) {
            logger.trace("raising timer event. (timerRegistration {})", timerRegistration);
        }

        timerEventSender.sendTimerEvent(timerRegistration.getOrderId(), timerRegistration.getTimerTag(), timerRegistration.getTimerEventTime());

        return true;
    }
}
