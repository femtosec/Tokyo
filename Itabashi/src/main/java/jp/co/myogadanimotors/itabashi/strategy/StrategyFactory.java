package jp.co.myogadanimotors.itabashi.strategy;

import jp.co.myogadanimotors.itabashi.strategy.peg.Peg;
import jp.co.myogadanimotors.myogadani.config.IConfigAccessor;
import jp.co.myogadanimotors.myogadani.store.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.IStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
import jp.co.myogadanimotors.myogadani.strategy.strategyparameter.BaseStrategyParameters;

public class StrategyFactory implements IStrategyFactory {
    @Override
    public IStrategy create(IStrategyDescriptor strategyDescriptor,
                            StrategyContext context,
                            IConfigAccessor strategyConfigAccessor) {
        switch (strategyDescriptor.getName()) {
            case "Peg":     // test
                return new Peg(strategyDescriptor, context, new BaseStrategyParameters(strategyDescriptor.getName(), strategyConfigAccessor));
        }

        return null;
    }
}
