package jp.co.myogadanimotors.kohinata.strategy.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

public abstract class BaseStrategyEvent extends BaseEvent<IStrategyEventListener> {

    public BaseStrategyEvent(long eventId, long creationTime, IStrategyEventListener eventListener) {
        super(eventId, creationTime, eventListener);
    }

    public abstract StrategyEventType getStrategyEventType();

    protected abstract void callStrategyEventListener(IStrategyEventListener strategyEventListener);

    @Override
    protected final void callEventListener(IStrategyEventListener eventListener) {
        eventListener.preProcessEvent(this);
        callStrategyEventListener(eventListener);
        eventListener.postProcessEvent();
    }
}
