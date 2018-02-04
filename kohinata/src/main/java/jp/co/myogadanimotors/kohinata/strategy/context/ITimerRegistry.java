package jp.co.myogadanimotors.kohinata.strategy.context;

public interface ITimerRegistry {
    void registerTimer(long timerTag, long timerEventTime);
    void registerRepetitiveTimer(long timerTag, long timerInterval, long timerStartTime);
    void registerRepetitiveTimer(long timerTag, long timerInterval, long timerStartTime, long timerEndTime);
    void removeRepetitiveTimer(long timerTag);
    boolean hasTimerRegistry(long timerTag);
}
