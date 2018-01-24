package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChildOrderContainer implements IChildOrderContainer {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final Map<Long, IOrder> ordersById = new ConcurrentHashMap<>();
    private final Map<String, Map<Long, IOrder>> ordersByIdAndTag = new ConcurrentHashMap<>();

    private int onTheWireOrdersCount = 0;

    public ChildOrderContainer() {

    }

    @Override
    public IOrder getChildOrder(long childOrderId) {
        return ordersById.get(childOrderId);
    }

    @Override
    public Collection<IOrder> getChildOrders() {
        return ordersById.values();
    }

    @Override
    public Collection<IOrder> getChildOrdersByTag(String childOrderTag) {
        if (!ordersByIdAndTag.containsKey(childOrderTag)) {
            ordersByIdAndTag.put(childOrderTag, new ConcurrentHashMap<>());
        }
        return ordersByIdAndTag.get(childOrderTag).values();
    }

    @Override
    public boolean hasOnTheWireChildOrders() {
        return (onTheWireOrdersCount > 0);
    }

    @Override
    public boolean contains(long orderId) {
        return ordersById.containsKey(orderId);
    }

    public void addChildOrder(IOrder childOrder) {
        // sanity check
        if (ordersById.containsKey(childOrder.getOrderId())) {
            logger.error("cannot add new child order. need to check the code. (childOrder: {})", childOrder);
            return;
        }

        ordersById.put(childOrder.getOrderId(), childOrder);

        if (!ordersByIdAndTag.containsKey(childOrder.getOrderTag())) {
            ordersByIdAndTag.put(childOrder.getOrderTag(), new ConcurrentHashMap<>());
        }

        ordersByIdAndTag.get(childOrder.getOrderTag()).put(childOrder.getOrderId(), childOrder);
    }

    public void updateChildOrder(IOrder childOrder) {
        // sanity check
        if (!ordersById.containsKey(childOrder.getOrderId())) {
            logger.error("cannot update the child order. need to check the code. (childOrder: {})", childOrder);
            return;
        }

        ordersById.put(childOrder.getOrderId(), childOrder);
        ordersByIdAndTag.get(childOrder.getOrderTag()).put(childOrder.getOrderId(), childOrder);
    }

    public void removeChildOrder(IOrder childOrder) {
        // sanity check
        if (!ordersById.containsKey(childOrder.getOrderId())) {
            logger.error("cannot remove the child order. need to check the code. (childOrder: {})", childOrder);
            return;
        }

        ordersById.remove(childOrder.getOrderId());
        ordersByIdAndTag.get(childOrder.getOrderTag()).remove(childOrder.getOrderId());
    }

    public void incrementOnTheWireOrdersCount() {
        onTheWireOrdersCount++;
    }

    public void decrementOnTheWireOrdersCount() {
        // sanity check
        if (onTheWireOrdersCount == 0) {
            logger.error("cannot decrement on-the-wire orders count. need to check the code.");
            return;
        }

        onTheWireOrdersCount--;
    }
}
