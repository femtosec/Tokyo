package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.IAsyncTimerRegistrationListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.TimerRegistrationSender;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimerRegistry implements ITimerRegistry {

    private final long orderId;
    private final TimerRegistrationSender timerRegistrationSender;
    private final List<Long> userTagList = new ArrayList<>();
    private final Map<Long, RepetitiveTimerEntry> repetitiveTimerEntryMapByUserTag = new ConcurrentHashMap<>();

    public TimerRegistry(long orderId,
                         EventIdGenerator eventIdGenerator,
                         ITimeSource timeSource,
                         IAsyncTimerRegistrationListener asyncTimerRegistrationListener) {
        this.orderId = orderId;
        timerRegistrationSender = new TimerRegistrationSender(eventIdGenerator, timeSource);
        timerRegistrationSender.addAsyncEventListener(asyncTimerRegistrationListener);
    }

    @Override
    public void registerTimer(long userTag, long timerEventTime) {
        timerRegistrationSender.sendTimerRegistration(orderId, userTag, timerEventTime);
        userTagList.add(userTag);
    }

    @Override
    public void registerRepetitiveTimer(long userTag, long timerInterval, long timerStartTime) {
        registerRepetitiveTimer(userTag, timerStartTime, Long.MAX_VALUE, timerInterval);
    }

    @Override
    public void registerRepetitiveTimer(long userTag, long timerInterval, long timerStartTime, long timerEndTime) {
        if (timerInterval <= 0) {
            throw new IllegalArgumentException("timerInterval must be more than 0. timerInterval: " + timerInterval);
        }

        if (timerEndTime < timerStartTime) {
            throw new IllegalArgumentException("timerEndTime must be more than or equal to timerStartTime. timerStartTime: "+ timerStartTime + ", timerEndTime: " + timerEndTime);
        }

        RepetitiveTimerEntry rte = new RepetitiveTimerEntry(userTag, timerInterval, timerStartTime, timerEndTime);
        if (repetitiveTimerEntryMapByUserTag.containsKey(userTag)) {
            repetitiveTimerEntryMapByUserTag.remove(userTag);
        }
        repetitiveTimerEntryMapByUserTag.put(userTag, rte);

        registerTimer(userTag, timerStartTime);
    }

    @Override
    public boolean hasTimerRegistry(long userTag) {
        return userTagList.contains(userTag);
    }

    public void onTimer(long userTag, long timerEventTime) {
        userTagList.remove(userTag);

        if (!repetitiveTimerEntryMapByUserTag.containsKey(userTag)) {
            return;
        }

        if (userTagList.contains(userTag)) {
            return;
        }

        RepetitiveTimerEntry rte = repetitiveTimerEntryMapByUserTag.get(userTag);
        if (timerEventTime < rte.timerStartTime) {
            return;
        }

        long nextTimerEventTime = timerEventTime + rte.timerInterval;
        if (rte.timerEndTime < nextTimerEventTime) {
            repetitiveTimerEntryMapByUserTag.remove(userTag);
        }

        registerTimer(userTag, nextTimerEventTime);
    }

    private class RepetitiveTimerEntry {
        private final long userTag;
        private final long timerInterval;
        private final long timerStartTime;
        private final long timerEndTime;

        private RepetitiveTimerEntry(long userTag, long timerInterval, long timerStartTime, long timerEndTime) {
            this.userTag = userTag;
            this.timerInterval = timerInterval;
            this.timerStartTime = timerStartTime;
            this.timerEndTime = timerEndTime;
        }
    }
}
