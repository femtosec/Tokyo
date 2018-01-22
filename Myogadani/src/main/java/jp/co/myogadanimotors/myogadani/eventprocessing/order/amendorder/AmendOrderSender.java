package jp.co.myogadanimotors.myogadani.eventprocessing.order.amendorder;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.math.BigDecimal;
import java.util.Map;

public class AmendOrderSender extends BaseEventSender<AmendOrder> {

    private long requestId;
    private long orderId;
    private BigDecimal orderQuantity;
    private BigDecimal priceLimit;
    private Map<String, String> extendedAttributes;

    public AmendOrderSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected AmendOrder createEvent(long eventId, long creationTime, IAsyncEventListener<AmendOrder> eventListener) {
        return new AmendOrder(
                eventId,
                creationTime,
                requestId,
                orderId,
                orderQuantity,
                priceLimit,
                extendedAttributes,
                eventListener
        );
    }

    public void sendAmendOrder(long requestId,
                               long orderId,
                               BigDecimal orderQuantity,
                               BigDecimal priceLimit,
                               Map<String, String> extendedAttributes) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.orderQuantity = orderQuantity;
        this.priceLimit = priceLimit;
        this.extendedAttributes = extendedAttributes;
        send();
    }
}
