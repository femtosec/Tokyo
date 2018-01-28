package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.myogadani.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;

public interface IStrategyFactory {
    IStrategy create(IStrategyDescriptor strategyDescriptor, StrategyContext strategyContext);
}
