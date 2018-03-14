package jp.co.myogadanimotors.kohinata.strategy.event.timer;

import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.AbstractStrategyEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyTimerEvent extends AbstractStrategyEvent {

    private final long timerTag;
    private final long timerEventTime;

    public StrategyTimerEvent(long eventId,
                              long creationTime,
                              StrategyContext context,
                              long timerTag,
                              long timerEventTime) {
        super(eventId, creationTime, context);
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
    protected void callEventProcessor(StrategyContext context) {
        context.processStrategyTimerEvent(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", timerTag: ").append(timerTag)
                .append(", timerEventTime: ").append(timerEventTime);
    }
}
