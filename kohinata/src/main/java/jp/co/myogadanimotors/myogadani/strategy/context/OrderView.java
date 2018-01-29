package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.kohinata.event.order.OrderSide;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OrderView implements IOrder {

    private final long orderId;
    private final long accountId;
    private final String symbol;
    private final String mic;
    private final String orderTag;
    private final OrderSide orderSide;
    private final Map<String, String> extendedAttributes = new ConcurrentHashMap<>();
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
        orderTag = order.getOrderTag();
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
        extendedAttributes.putAll(order.getExtendedAttributes());
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

    public void updateExtendedAttributes(Map<String, String> extendedAttributes) {
        this.extendedAttributes.clear();
        this.extendedAttributes.putAll(extendedAttributes);
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
    public String getOrderTag() {
        return orderTag;
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
    public String getExtendedAttribute(String key) {
        return extendedAttributes.get(key);
    }

    @Override
    public Map<String, String> getExtendedAttributes() {
        return new ConcurrentHashMap<>(extendedAttributes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("orderId: ").append(orderId)
                .append(", accountId: ").append(accountId)
                .append(", symbol: ").append(symbol)
                .append(", mic: ").append(mic)
                .append(", orderTag: ").append(orderTag)
                .append(", orderSide: ").append(orderSide)
                .append(", orderQuantity: ").append(orderQuantity)
                .append(", execQuantity: ").append(execQuantity)
                .append(", cancelledQuantity: ").append(cancelledQuantity)
                .append(", expiredQuantity: ").append(expiredQuantity)
                .append(", rejectedQuantity: ").append(rejectedQuantity)
                .append(", exposedQuantity: ").append(exposedQuantity)
                .append(", remainingQuantity: ").append(getRemainingQuantity())
                .append(", priceLimit: ").append(priceLimit);

        for (Map.Entry<String, String> entry : extendedAttributes.entrySet()) {
            sb.append(", [").append(entry.getKey()).append(":").append(entry.getValue()).append("]");
        }

        return sb.toString();
    }
}
