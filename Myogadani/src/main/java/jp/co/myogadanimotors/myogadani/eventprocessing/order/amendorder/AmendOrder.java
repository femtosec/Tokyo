package jp.co.myogadanimotors.myogadani.eventprocessing.order.amendorder;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AmendOrder extends BaseEvent<IAsyncEventListener<AmendOrder>> {

    private final long requestId;
    private final long orderId;
    private final BigDecimal orderQuantity;
    private final BigDecimal priceLimit;
    private final Map<String, String> extendedAttributes = new ConcurrentHashMap<>();

    public AmendOrder(long requestId,
                      long eventId,
                      long creationTime,
                      long orderId,
                      BigDecimal orderQuantity,
                      BigDecimal priceLimit,
                      Map<String, String> extendedAttributes,
                      IAsyncEventListener<AmendOrder> eventListener) {
        super(eventId, creationTime, eventListener);
        this.requestId = requestId;
        this.orderId = orderId;
        this.orderQuantity = orderQuantity;
        this.priceLimit = priceLimit;
        this.extendedAttributes.putAll(extendedAttributes);
    }

    public long getRequestId() {
        return requestId;
    }

    public long getOrderId() {
        return orderId;
    }

    public BigDecimal getOrderQuantity() {
        return orderQuantity;
    }

    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    public Map<String, String> getExtendedAttributes() {
        return new ConcurrentHashMap<>(extendedAttributes);
    }

    public String getExtendedAttribute(String key) {
        return extendedAttributes.get(key);
    }

    @Override
    public StringBuilder toStringBuilder() {
        StringBuilder sb = super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", orderId: ").append(orderId)
                .append(", orderQuantity: ").append(orderQuantity)
                .append(", priceLimit: ").append(priceLimit);

        if (extendedAttributes != null) {
            for (Map.Entry<String, String> entry : extendedAttributes.entrySet()) {
                sb.append(", [").append(entry.getKey()).append(":").append(entry.getValue()).append("]");
            }
        }

        return sb;
    }
}
