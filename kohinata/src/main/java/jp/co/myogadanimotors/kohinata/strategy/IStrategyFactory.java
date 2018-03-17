package jp.co.myogadanimotors.kohinata.strategy;

@FunctionalInterface
public interface IStrategyFactory {
    IStrategy create(String strategyName);
}
