package jp.co.myogadanimotors.myogadani.ordermanagement.order;

import jp.co.myogadanimotors.myogadani.common.OrderSide;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Order implements IOrder {

    private final long orderId;
    private final long accountId;
    private final String symbol;
    private final String mic;
    private final OrderSide orderSide;
    private final Orderer orderer;
    private final OrderDestination destination;
    private final Map<String, String> extendedAttributes = new ConcurrentHashMap<>();
    private final Order parentStrategyOrder;
    private final int threadId;

    private BigDecimal orderQuantity;
    private BigDecimal execQuantity;
    private BigDecimal cancelledQuantity;
    private BigDecimal expiredQuantity;
    private BigDecimal rejectedQuantity;
    private BigDecimal exposedQuantity;
    private BigDecimal priceLimit;
    private OrderState orderState = OrderState.New;
    private int version = 0;
    private IStrategy strategy;

    public Order(long orderId,
                 long accountId,
                 String symbol,
                 String mic,
                 OrderSide orderSide,
                 BigDecimal orderQuantity,
                 BigDecimal priceLimit,
                 Orderer orderer,
                 OrderDestination destination,
                 Map<String, String> extendedAttributes,
                 Order parentStrategyOrder,
                 int threadId) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.mic = mic;
        this.orderSide = orderSide;
        this.orderQuantity = orderQuantity;
        this.execQuantity = BigDecimal.ZERO;
        this.cancelledQuantity = BigDecimal.ZERO;
        this.expiredQuantity = BigDecimal.ZERO;
        this.rejectedQuantity = BigDecimal.ZERO;
        this.exposedQuantity = BigDecimal.ZERO;
        this.priceLimit = priceLimit;
        this.orderer = orderer;
        this.destination = destination;
        this.extendedAttributes.putAll(extendedAttributes);
        this.parentStrategyOrder = parentStrategyOrder;
        this.threadId = threadId;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // setters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setOrderQuantity(BigDecimal orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public void setExecQuantity(BigDecimal execQuantity) {
        this.execQuantity = execQuantity;
    }

    public void setCancelledQuantity(BigDecimal cancelledQuantity) {
        this.cancelledQuantity = cancelledQuantity;
    }

    public void setExpiredQuantity(BigDecimal expiredQuantity) {
        this.expiredQuantity = expiredQuantity;
    }

    public void setRejectedQuantity(BigDecimal rejectedQuantity) {
        this.rejectedQuantity = rejectedQuantity;
    }

    public void setExposedQuantity(BigDecimal exposedQuantity) {
        this.exposedQuantity = exposedQuantity;
    }

    public void setPriceLimit(BigDecimal priceLimit) {
        this.priceLimit = priceLimit;
    }

    public void setStrategy(IStrategy strategy) {
        this.strategy = strategy;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public void setExtendedAttributes(Map<String, String> extendedAttributes) {
        this.extendedAttributes.clear();
        this.extendedAttributes.putAll(extendedAttributes);
    }

    public void incrementVersion() {
        version++;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

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
    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    public Orderer getOrderer() {
        return orderer;
    }

    public OrderDestination getDestination() {
        return destination;
    }

    public boolean isStrategyOrder() {
        return (destination != null && destination == OrderDestination.Strategy);
    }

    public Order getParentStrategyOrder() {
        return parentStrategyOrder;
    }

    public IStrategy getStrategy() {
        return strategy;
    }

    public int getThreadId() {
        return threadId;
    }

    public Map<String, String> getExtendedAttributes() {
        return new ConcurrentHashMap<>(extendedAttributes);
    }

    @Override
    public String toString() {
        // todo: add more
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
