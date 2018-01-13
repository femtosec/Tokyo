package jp.co.myogadanimotors.myogadani.eventprocessing.strategyinvoker;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventType;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;

public final class StrategyInvoker extends AbstractEvent {

    private final IStrategyEvent strategyEvent;
    private final IStrategy strategy;

    public StrategyInvoker(long eventId, long creationTime, String eventSenderName, IStrategyEvent strategyEvent, IStrategy strategy) {
        super(eventId, creationTime, eventSenderName);
        this.strategyEvent = strategyEvent;
        this.strategy = strategy;
    }

    @Override
    public EventType getEventType() {
        return EventType.StrategyInvocation;
    }

    public void invokeStrategy() {
        strategy.processStrategyEvent(strategyEvent);
    }
}
