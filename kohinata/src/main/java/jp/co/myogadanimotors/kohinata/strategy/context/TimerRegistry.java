package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerRegistrationListener;
import jp.co.myogadanimotors.kohinata.event.timer.TimerRegistrationSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TimerRegistry implements ITimerRegistry {

    private final long orderId;
    private final TimerRegistrationSender timerRegistrationSender;
    private final List<Long> timerTagList = new ArrayList<>();
    private final Map<Long, RepetitiveTimerEntry> repetitiveTimerEntryMapByTimerTag = new ConcurrentHashMap<>();

    public TimerRegistry(long orderId,
                         EventIdGenerator eventIdGenerator,
                         ITimeSource timeSource,
                         IAsyncTimerRegistrationListener asyncTimerRegistrationListener) {
        this.orderId = orderId;
        timerRegistrationSender = new TimerRegistrationSender(eventIdGenerator, timeSource);
        timerRegistrationSender.addAsyncEventListener(asyncTimerRegistrationListener);
    }

    @Override
    public void registerTimer(long timerTag, long timerEventTime) {
        timerRegistrationSender.sendTimerRegistration(orderId, timerTag, timerEventTime);
        timerTagList.add(timerTag);
    }

    @Override
    public void registerRepetitiveTimer(long timerTag, long timerInterval, long timerStartTime) {
        registerRepetitiveTimer(timerTag, timerStartTime, Long.MAX_VALUE, timerInterval);
    }

    @Override
    public void registerRepetitiveTimer(long timerTag, long timerInterval, long timerStartTime, long timerEndTime) {
        if (timerInterval <= 0) {
            throw new IllegalArgumentException("timerInterval must be more than 0. timerInterval: " + timerInterval);
        }

        if (timerEndTime < timerStartTime) {
            throw new IllegalArgumentException("timerEndTime must be more than or equal to timerStartTime. timerStartTime: "+ timerStartTime + ", timerEndTime: " + timerEndTime);
        }

        RepetitiveTimerEntry rte = new RepetitiveTimerEntry(timerTag, timerInterval, timerStartTime, timerEndTime);
        if (repetitiveTimerEntryMapByTimerTag.containsKey(timerTag)) {
            repetitiveTimerEntryMapByTimerTag.remove(timerTag);
        }
        repetitiveTimerEntryMapByTimerTag.put(timerTag, rte);

        registerTimer(timerTag, timerStartTime);
    }

    @Override
    public void removeRepetitiveTimer(long timerTag) {
        repetitiveTimerEntryMapByTimerTag.remove(timerTag);
    }

    @Override
    public boolean hasTimerRegistry(long timerTag) {
        return timerTagList.contains(timerTag);
    }

    public void onTimer(long timerTag, long timerEventTime) {
        timerTagList.remove(timerTag);

        if (!repetitiveTimerEntryMapByTimerTag.containsKey(timerTag)) {
            return;
        }

        if (timerTagList.contains(timerTag)) {
            return;
        }

        RepetitiveTimerEntry rte = repetitiveTimerEntryMapByTimerTag.get(timerTag);
        if (timerEventTime < rte.timerStartTime) {
            return;
        }

        long nextTimerEventTime = timerEventTime + rte.timerInterval;
        if (rte.timerEndTime < nextTimerEventTime) {
            repetitiveTimerEntryMapByTimerTag.remove(timerTag);
        }

        registerTimer(timerTag, nextTimerEventTime);
    }

    private class RepetitiveTimerEntry {
        private final long timerTag;
        private final long timerInterval;
        private final long timerStartTime;
        private final long timerEndTime;

        private RepetitiveTimerEntry(long timerTag, long timerInterval, long timerStartTime, long timerEndTime) {
            this.timerTag = timerTag;
            this.timerInterval = timerInterval;
            this.timerStartTime = timerStartTime;
            this.timerEndTime = timerEndTime;
        }
    }
}
