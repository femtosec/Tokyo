package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;

public interface IStrategy {
    int getStrategyTypeId();
    void processStrategyEvent(IStrategyEvent strategyEvent);
}
