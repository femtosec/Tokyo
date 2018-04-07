package jp.co.myogadanimotors.kohinata.strategy.event.timer;

import jp.co.myogadanimotors.kohinata.strategy.event.BaseStrategyEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyTimerEvent extends BaseStrategyEvent {

    private final long timerTag;
    private final long timerEventTime;

    public StrategyTimerEvent(long eventId,
                              long creationTime,
                              IStrategyEventListener strategyEventListener,
                              long timerTag,
                              long timerEventTime) {
        super(eventId, creationTime, strategyEventListener);
        this.timerTag = timerTag;
        this.timerEventTime = timerEventTime;
    }

    public long getTimerTag() {
        return timerTag;
    }

    public long getTimerEventTime() {
        return timerEventTime;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.Timer;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyTimerEvent(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", timerTag: ").append(timerTag)
                .append(", timerEventTime: ").append(timerEventTime);
    }
}
