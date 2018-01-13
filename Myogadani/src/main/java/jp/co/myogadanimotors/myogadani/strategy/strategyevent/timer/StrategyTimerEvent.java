package jp.co.myogadanimotors.myogadani.strategy.strategyevent.timer;

import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyTimerEvent implements IStrategyEvent {

    private final long userTag;
    private final long timerEventTime;

    public StrategyTimerEvent(long userTag, long timerEventTime) {
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.Timer;
    }

    public long getUserTag() {
        return userTag;
    }

    public long getTimerEventTime() {
        return timerEventTime;
    }
}
