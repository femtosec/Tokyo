package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;

import java.util.Collection;

public interface IChildOrderContainer {
    IOrder getChildOrder(long orderId);
    Collection<IOrder> getChildOrders();
    Collection<IOrder> getChildOrdersByTag(String childOrderTag);
    boolean hasOnTheWireChildOrders();
    boolean hasExposedChildOrders();
    boolean contains(long orderId);
}
