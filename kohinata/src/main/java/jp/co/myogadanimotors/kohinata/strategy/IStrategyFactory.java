package jp.co.myogadanimotors.kohinata.strategy;

public interface IStrategyFactory {
    IStrategy create(String strategyName);
}
