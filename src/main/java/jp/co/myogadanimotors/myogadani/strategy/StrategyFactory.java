package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.IStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.peg.Peg;

public class StrategyFactory implements IStrategyFactory {
    @Override
    public IStrategy create(String strategyName) {
        switch (strategyName) {
            case "Peg":     // test
                return new Peg();
        }

        return null;
    }
}
