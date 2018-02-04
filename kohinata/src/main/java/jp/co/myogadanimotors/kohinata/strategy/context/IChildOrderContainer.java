package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;

import java.util.Collection;

public interface IChildOrderContainer {
    IOrder getChildOrder(long orderId);
    Collection<IOrder> getChildOrders();
    Collection<IOrder> getChildOrdersByTag(String childOrderTag);
    boolean hasOnTheWireChildOrders();
    boolean hasExposedChildOrders();
    boolean contains(long orderId);
}
