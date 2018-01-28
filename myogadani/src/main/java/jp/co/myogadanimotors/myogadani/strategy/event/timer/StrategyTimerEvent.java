package jp.co.myogadanimotors.myogadani.strategy.event.timer;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.event.AbstractStrategyEvent;

public final class StrategyTimerEvent extends AbstractStrategyEvent {

    private final long timerTag;
    private final long timerEventTime;

    public StrategyTimerEvent(long eventId,
                              long creationTime,
                              IStrategy strategy,
                              long timerTag,
                              long timerEventTime) {
        super(eventId, creationTime, strategy);
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
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyTimerEvent(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", timerTag: ").append(timerTag)
                .append(", timerEventTime: ").append(timerEventTime);
    }
}
