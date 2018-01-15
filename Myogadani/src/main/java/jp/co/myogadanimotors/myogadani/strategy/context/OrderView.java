package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.common.OrderSide;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.OrderState;

import java.math.BigDecimal;

public final class OrderView implements IOrder {

    private final long orderId;
    private final long accountId;
    private final String symbol;
    private final String mic;
    private final OrderSide orderSide;
    private BigDecimal orderQuantity;
    private BigDecimal execQuantity;
    private BigDecimal cancelledQuantity;
    private BigDecimal expiredQuantity;
    private BigDecimal rejectedQuantity;
    private BigDecimal exposedQuantity;
    private BigDecimal priceLimit;
    private OrderState orderState;
    private int version;

    public OrderView(IOrder order) {
        orderId = order.getOrderId();
        accountId = order.getAccountId();
        symbol = order.getSymbol();
        mic = order.getMic();
        orderSide = order.getOrderSide();
        orderQuantity = order.getOrderQuantity();
        execQuantity = order.getExecQuantity();
        cancelledQuantity = order.getCancelledQuantity();
        expiredQuantity = order.getExpiredQuantity();
        rejectedQuantity = order.getRejectedQuantity();
        exposedQuantity = order.getExposedQuantity();
        priceLimit = order.getPriceLimit();
        orderState = order.getOrderState();
        version = order.getVersion();
    }

    public void update(IOrder order) {
        orderQuantity = order.getOrderQuantity();
        execQuantity = order.getExecQuantity();
        cancelledQuantity = order.getCancelledQuantity();
        expiredQuantity = order.getExpiredQuantity();
        rejectedQuantity = order.getRejectedQuantity();
        exposedQuantity = order.getExposedQuantity();
        priceLimit = order.getPriceLimit();
        orderState = order.getOrderState();
        version = order.getVersion();
    }

    @Override
    public long getOrderId() {
        return orderId;
    }

    @Override
    public OrderState getOrderState() {
        return orderState;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public long getAccountId() {
        return accountId;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getMic() {
        return mic;
    }

    @Override
    public OrderSide getOrderSide() {
        return orderSide;
    }

    @Override
    public BigDecimal getOrderQuantity() {
        return orderQuantity;
    }

    @Override
    public BigDecimal getExecQuantity() {
        return execQuantity;
    }

    @Override
    public BigDecimal getCancelledQuantity() {
        return cancelledQuantity;
    }

    @Override
    public BigDecimal getExpiredQuantity() {
        return expiredQuantity;
    }

    @Override
    public BigDecimal getRejectedQuantity() {
        return rejectedQuantity;
    }

    @Override
    public BigDecimal getExposedQuantity() {
        return exposedQuantity;
    }

    @Override
    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("orderId: ").append(orderId)
                .append(", accountId: ").append(accountId)
                .append(", symbol: ").append(symbol)
                .append(", mic: ").append(mic)
                .append(", orderSide: ").append(orderSide)
                .append(", orderQuantity: ").append(orderQuantity)
                .append(", execQuantity: ").append(execQuantity)
                .append(", cancelledQuantity: ").append(cancelledQuantity)
                .append(", expiredQuantity: ").append(expiredQuantity)
                .append(", rejectedQuantity: ").append(rejectedQuantity)
                .append(", exposedQuantity: ").append(exposedQuantity)
                .append(", remainingQuantity: ").append(getRemainingQuantity())
                .append(", priceLimit: ").append(priceLimit);
        return sb.toString();
    }
}
