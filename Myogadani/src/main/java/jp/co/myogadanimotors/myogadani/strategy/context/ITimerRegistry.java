package jp.co.myogadanimotors.myogadani.strategy.context;

public interface ITimerRegistry {
    void registerTimer(long timerTag, long timerEventTime);
    void registerRepetitiveTimer(long timerTag, long timerInterval, long timerStartTime);
    void registerRepetitiveTimer(long timerTag, long timerInterval, long timerStartTime, long timerEndTime);
    boolean hasTimerRegistry(long timerTag);
}
