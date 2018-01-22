package jp.co.myogadanimotors.myogadani.strategy.strategyevent.timer;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.AbstractStrategyEvent;

public final class StrategyTimerEvent extends AbstractStrategyEvent {

    private final long userTag;
    private final long timerEventTime;

    public StrategyTimerEvent(long eventId,
                              long creationTime,
                              IStrategy strategy,
                              long userTag,
                              long timerEventTime) {
        super(eventId, creationTime, strategy);
        this.userTag = userTag;
        this.timerEventTime = timerEventTime;
    }

    public long getUserTag() {
        return userTag;
    }

    public long getTimerEventTime() {
        return timerEventTime;
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyTimerEvent(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", userTag: ").append(userTag)
                .append(", timerEventTime: ").append(timerEventTime);
    }
}
