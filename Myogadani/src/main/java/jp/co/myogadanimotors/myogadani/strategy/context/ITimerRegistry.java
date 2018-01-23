package jp.co.myogadanimotors.myogadani.strategy.context;

public interface ITimerRegistry {
    void registerTimer(long userTag, long timerEventTime);
    void registerRepetitiveTimer(long userTag, long timerInterval, long timerStartTime);
    void registerRepetitiveTimer(long userTag, long timerInterval, long timerStartTime, long timerEndTime);
    boolean hasTimerRegistry(long userTag);
}
