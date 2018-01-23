package jp.co.myogadanimotors.myogadani.strategy.strategyevent;

import jp.co.myogadanimotors.myogadani.eventprocessing.IEvent;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;

public abstract class AbstractStrategyEvent implements IEvent {

    private final long eventId;
    private final long creationTime;
    private final IStrategy strategy;

    public AbstractStrategyEvent(long eventId, long creationTime, IStrategy strategy) {
        this.eventId = eventId;
        this.creationTime = creationTime;
        this.strategy = strategy;
    }

    protected abstract void callEventListener(IStrategy strategy);

    @Override
    public void run() {
        strategy.preProcessEvent();
        callEventListener(strategy);
        strategy.postProcessEvent();
    }

    @Override
    public final long getEventId() {
        return eventId;
    }

    @Override
    public final long getCreationTime() {
        return creationTime;
    }

    @Override
    public final String toString() {
        return toStringBuilder().toString();
    }

    @Override
    public StringBuilder toStringBuilder() {
        return new StringBuilder(getClass().getName())
                .append(" eventId: ").append(eventId)
                .append(", creationTime: ").append(creationTime)
                .append(", strategyName: ").append(strategy.getStrategyDescriptor().getName());
    }
}
