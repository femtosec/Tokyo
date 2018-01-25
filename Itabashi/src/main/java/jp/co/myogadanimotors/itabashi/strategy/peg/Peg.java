package jp.co.myogadanimotors.itabashi.strategy.peg;

import jp.co.myogadanimotors.myogadani.store.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.strategy.AbstractStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
import jp.co.myogadanimotors.myogadani.strategy.strategyparameter.IStrategyParameters;

public class Peg extends AbstractStrategy {

    public Peg(IStrategyDescriptor strategyDescriptor, StrategyContext context, IStrategyParameters strategyParameters) {
        super(strategyDescriptor, context, strategyParameters);
    }
}
