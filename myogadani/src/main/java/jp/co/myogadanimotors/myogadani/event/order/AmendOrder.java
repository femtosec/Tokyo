package jp.co.myogadanimotors.myogadani.event.order;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AmendOrder extends BaseEvent<IAsyncOrderListener> {

    private final long requestId;
    private final long orderId;
    private final BigDecimal orderQuantity;
    private final BigDecimal priceLimit;
    private final Map<String, String> extendedAttributes = new ConcurrentHashMap<>();

    public AmendOrder(long eventId,
                      long creationTime,
                      long requestId,
                      long orderId,
                      BigDecimal orderQuantity,
                      BigDecimal priceLimit,
                      Map<String, String> extendedAttributes,
                      IAsyncOrderListener eventListener) {
        super(eventId, creationTime, eventListener);
        this.requestId = requestId;
        this.orderId = orderId;
        this.orderQuantity = orderQuantity;
        this.priceLimit = priceLimit;
        if (extendedAttributes != null) {
            this.extendedAttributes.putAll(extendedAttributes);
        }
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
    protected void callEventListener(IAsyncOrderListener eventListener) {
        eventListener.processAmendOrder(this);
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
