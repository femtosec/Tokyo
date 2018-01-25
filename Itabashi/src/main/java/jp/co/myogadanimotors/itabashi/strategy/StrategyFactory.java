package jp.co.myogadanimotors.itabashi.strategy;

import jp.co.myogadanimotors.itabashi.strategy.peg.Peg;
import jp.co.myogadanimotors.myogadani.store.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.IStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;

public class StrategyFactory implements IStrategyFactory {
    @Override
    public IStrategy create(IStrategyDescriptor strategyDescriptor, StrategyContext context) {
        // todo: to be implemented
        switch (strategyDescriptor.getName()) {
            case "Peg":     // test
                return new Peg(strategyDescriptor, context);
        }

        return null;
    }
}
