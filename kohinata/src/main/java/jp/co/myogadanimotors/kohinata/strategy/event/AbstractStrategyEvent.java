package jp.co.myogadanimotors.kohinata.strategy.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;

import static java.util.Objects.requireNonNull;

public abstract class AbstractStrategyEvent implements IEvent {

    private final long eventId;
    private final long creationTime;
    private final StrategyContext context;

    public AbstractStrategyEvent(long eventId, long creationTime, StrategyContext context) {
        this.eventId = eventId;
        this.creationTime = creationTime;
        this.context = requireNonNull(context);
    }

    public abstract StrategyEventType getStrategyEventType();

    protected abstract void callEventProcessor(StrategyContext context);

    @Override
    public void run() {
        context.preProcessEvent(this);
        callEventProcessor(context);
        context.postProcessEvent();
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
        return new StringBuilder()
                .append("eventType: ").append(getStrategyEventType())
                .append(", eventId: ").append(eventId)
                .append(", creationTime: ").append(creationTime)
                .append(", strategyName: ").append(context.getStrategyDescriptor().getName());
    }
}
