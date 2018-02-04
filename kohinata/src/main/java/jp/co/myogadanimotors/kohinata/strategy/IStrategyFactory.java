package jp.co.myogadanimotors.kohinata.strategy;

import jp.co.myogadanimotors.kohinata.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;

public interface IStrategyFactory {
    IStrategy create(IStrategyDescriptor strategyDescriptor, StrategyContext strategyContext);
}
