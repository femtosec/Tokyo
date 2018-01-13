package jp.co.myogadanimotors.myogadani.ordermanagement.order;

import jp.co.myogadanimotors.myogadani.common.OrderSide;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Order extends BaseOrder {

    private final Orderer orderer;
    private final OrderDestination destination;
    private final Map<String, String> extendedAttributes = new ConcurrentHashMap<>();
    private final Order parentStrategyOrder;
    private final int threadId;
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
        super(
                orderId,
                accountId,
                symbol,
                mic,
                orderSide,
                orderQuantity,
                new BigDecimal(0),
                new BigDecimal(0),
                new BigDecimal(0),
                new BigDecimal(0),
                priceLimit
        );
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
        extendedAttributes.clear();
        extendedAttributes.putAll(extendedAttributes);
    }

    public void incrementVersion() {
        version++;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public Orderer getOrderer() {
        return orderer;
    }

    public OrderDestination getDestination() {
        return destination;
    }

    public boolean isStrategyOrder() {
        return (destination == OrderDestination.Strategy);
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
        Map<String, String> copy = new ConcurrentHashMap<>(extendedAttributes);
        return copy;
    }
}
