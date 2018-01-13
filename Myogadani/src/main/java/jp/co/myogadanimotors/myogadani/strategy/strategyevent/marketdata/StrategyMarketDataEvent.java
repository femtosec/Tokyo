package jp.co.myogadanimotors.myogadani.strategy.strategyevent.marketdata;

import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyMarketDataEvent implements IStrategyEvent {
    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.MarketData;
    }
}
