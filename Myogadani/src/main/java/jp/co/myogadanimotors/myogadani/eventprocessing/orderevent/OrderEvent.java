package jp.co.myogadanimotors.myogadani.eventprocessing.orderevent;

import jp.co.myogadanimotors.myogadani.common.OrderSide;
import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OrderEvent extends AbstractEvent {

    private final OrderType orderType;
    private final long orderId;
    private final long parentOrderId;
    private final long requestId;
    private final long accountId;
    private final String symbol;
    private final String mic;
    private final OrderSide orderSide;
    private final BigDecimal orderQuantity;
    private final BigDecimal priceLimit;
    private final Orderer orderer;
    private final OrderDestination destination;
    private final Map<String, String> extendedAttributes = new ConcurrentHashMap<>();

    public OrderEvent(long eventId,
                      long creationTime,
                      String eventSenderName,
                      OrderType orderType,
                      long orderId,
                      long parentOrderId,
                      long requestId,
                      long accountId,
                      String symbol,
                      String mic,
                      OrderSide orderSide,
                      BigDecimal orderQuantity,
                      BigDecimal priceLimit,
                      Orderer orderer,
                      OrderDestination destination,
                      Map<String, String> extendedAttributes) {
        super(eventId, creationTime, eventSenderName);
        this.orderType = orderType;
        this.orderId = orderId;
        this.parentOrderId = parentOrderId;
        this.requestId = requestId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.mic = mic;
        this.orderSide = orderSide;
        this.orderQuantity = orderQuantity;
        this.priceLimit = priceLimit;
        this.orderer = orderer;
        this.destination = destination;
        this.extendedAttributes.putAll(extendedAttributes);
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getParentOrderId() {
        return parentOrderId;
    }

    public long getRequestId() {
        return requestId;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getMic() {
        return mic;
    }

    public OrderSide getOrderSide() {
        return orderSide;
    }

    public BigDecimal getOrderQuantity() {
        return orderQuantity;
    }

    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    public Orderer getOrderer() {
        return orderer;
    }

    public OrderDestination getDestination() {
        return destination;
    }

    public Map<String, String> getExtendedAttributes() {
        return new ConcurrentHashMap<>(extendedAttributes);
    }

    @Override
    public EventType getEventType() {
        return EventType.Order;
    }

    @Override
    public StringBuilder toStringBuilder() {
        StringBuilder sb = super.toStringBuilder()
                .append(", orderId: ").append(orderId)
                .append(", accountId: ").append(accountId)
                .append(", orderType: ").append(orderType)
                .append(", symbol: ").append(symbol)
                .append(", mic: ").append(mic)
                .append(", orderSide: ").append(orderSide)
                .append(", orderQuantity: ").append(orderQuantity)
                .append(", priceLimit: ").append(priceLimit)
                .append(", orderer: ").append(orderer)
                .append(", destination: ").append(destination);

        if (extendedAttributes != null) {
            for (Map.Entry<String, String> entry : extendedAttributes.entrySet()) {
                sb.append(", [").append(entry.getKey()).append(":").append(entry.getValue()).append("]");
            }
        }

        return sb;
    }
}
