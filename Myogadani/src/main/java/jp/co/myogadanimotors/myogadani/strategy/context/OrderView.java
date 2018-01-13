package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.ordermanagement.order.BaseOrder;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;

public final class OrderView extends BaseOrder {

    public OrderView(IOrder order) {
        super(
                order.getOrderId(),
                order.getAccountId(),
                order.getSymbol(),
                order.getMic(),
                order.getOrderSide(),
                order.getOrderQuantity(),
                order.getExecQuantity(),
                order.getCancelledQuantity(),
                order.getExpiredQuantity(),
                order.getRejectedQuantity(),
                order.getPriceLimit()
        );
        orderState = order.getOrderState();
        version = order.getVersion();
    }

    public void update(IOrder order) {
        orderQuantity = order.getOrderQuantity();
        priceLimit = order.getPriceLimit();
        orderState = order.getOrderState();
        version = order.getVersion();
    }
}
