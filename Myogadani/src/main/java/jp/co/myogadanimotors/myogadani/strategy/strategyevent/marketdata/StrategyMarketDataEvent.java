package jp.co.myogadanimotors.myogadani.strategy.strategyevent.marketdata;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.AbstractStrategyEvent;

public final class StrategyMarketDataEvent extends AbstractStrategyEvent {

    public StrategyMarketDataEvent(long eventId, long creationTime, IStrategy strategy) {
        super(eventId, creationTime, strategy);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyMarketDataEvent(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder();
    }
}
