package jp.co.myogadanimotors.itabashi.strategy.peg;

import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.strategy.AbstractStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;

public class Peg extends AbstractStrategy {

    public Peg(IStrategyDescriptor strategyDescriptor, StrategyContext context) {
        super(strategyDescriptor, context);
    }
}
