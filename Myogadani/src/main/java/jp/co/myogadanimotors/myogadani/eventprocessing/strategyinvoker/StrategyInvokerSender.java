package jp.co.myogadanimotors.myogadani.eventprocessing.strategyinvoker;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventStream;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public final class StrategyInvokerSender extends AbstractEventSender<StrategyInvoker> {

    private IStrategyEvent strategyEvent;
    private IStrategy strategy;
    
    public StrategyInvokerSender(String eventSenderName, IEventIdGenerator idGenerator, ITimeSource timeSource, IEventStream eventStream) {
        super(eventSenderName, idGenerator, timeSource, eventStream);
    }

    @Override
    protected StrategyInvoker createEvent() {
        return new StrategyInvoker(
                generateEventId(),
                getCurrentTime(),
                getEventSenderName(),
                strategyEvent,
                strategy
        );
    }

    public void sendStrategyInvoker(IStrategyEvent strategyEvent, IStrategy strategy) {
        this.strategyEvent = strategyEvent;
        this.strategy = strategy;
        send();
    }
}
