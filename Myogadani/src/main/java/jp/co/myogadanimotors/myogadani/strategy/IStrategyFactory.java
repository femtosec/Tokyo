package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;

public interface IStrategyFactory {
    IStrategy create(String strategyName, IOrder order);
}
