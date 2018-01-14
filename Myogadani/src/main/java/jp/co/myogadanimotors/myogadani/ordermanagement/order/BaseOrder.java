package jp.co.myogadanimotors.myogadani.ordermanagement.order;

import jp.co.myogadanimotors.myogadani.common.OrderSide;

import java.math.BigDecimal;

public class BaseOrder implements IOrder {

    private final long orderId;

    protected OrderState orderState = OrderState.New;
    protected int version = 0;
    protected long accountId;
    protected String symbol;
    protected String mic;
    protected OrderSide orderSide;
    protected BigDecimal orderQuantity;
    protected BigDecimal execQuantity;
    protected BigDecimal cancelledQuantity;
    protected BigDecimal expiredQuantity;
    protected BigDecimal rejectedQuantity;
    protected BigDecimal exposedQuantity;
    protected BigDecimal priceLimit;

    public BaseOrder(long orderId,
                     long accountId,
                     String symbol,
                     String mic,
                     OrderSide orderSide,
                     BigDecimal orderQuantity,
                     BigDecimal execQuantity,
                     BigDecimal cancelledQuantity,
                     BigDecimal expiredQuantity,
                     BigDecimal rejectedQuantity,
                     BigDecimal exposedQuantity,
                     BigDecimal priceLimit) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.mic = mic;
        this.orderSide = orderSide;
        this.orderQuantity = orderQuantity;
        this.execQuantity = execQuantity;
        this.cancelledQuantity = cancelledQuantity;
        this.expiredQuantity = expiredQuantity;
        this.rejectedQuantity = rejectedQuantity;
        this.exposedQuantity = exposedQuantity;
        this.priceLimit = priceLimit;
    }

    @Override
    public long getOrderId() {
        return orderId;
    }

    @Override
    public long getAccountId() {
        return accountId;
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
    public BigDecimal getRemainingQuantity() {
        return getOrderQuantity().subtract(getExecQuantity());
    }

    @Override
    public BigDecimal getAvailableQuantity() {
        return getRemainingQuantity().subtract(getExposedQuantity());
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
