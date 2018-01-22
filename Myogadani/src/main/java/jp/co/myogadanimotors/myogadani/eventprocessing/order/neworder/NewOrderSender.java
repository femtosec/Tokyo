package jp.co.myogadanimotors.myogadani.eventprocessing.order.neworder;

import jp.co.myogadanimotors.myogadani.common.OrderSide;
import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NewOrderSender extends BaseEventSender<NewOrder> {

    private long requestId;
    private long parentOrderId;
    private long accountId;
    private String symbol;
    private String mic;
    private OrderSide orderSide;
    private BigDecimal orderQuantity;
    private BigDecimal priceLimit;
    private Orderer orderer;
    private OrderDestination destination;
    private Map<String, String> extendedAttributes = new ConcurrentHashMap<>();

    public NewOrderSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected NewOrder createEvent(long eventId, long creationTime, IAsyncEventListener<NewOrder> eventListener) {
        return new NewOrder(
                eventId,
                creationTime,
                requestId,
                parentOrderId,
                accountId,
                symbol,
                mic,
                orderSide,
                orderQuantity,
                priceLimit,
                orderer,
                destination,
                extendedAttributes,
                eventListener
        );
    }

    public void sendNewOrder(long requestId,
                             long parentOrderId,
                             long accountId,
                             String symbol,
                             String mic,
                             OrderSide orderSide,
                             BigDecimal orderQuantity,
                             BigDecimal priceLimit,
                             Orderer orderer,
                             OrderDestination destination,
                             Map<String, String> extendedAttributes) {
        this.requestId = requestId;
        this.parentOrderId = parentOrderId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.mic = mic;
        this.orderSide = orderSide;
        this.orderQuantity = orderQuantity;
        this.priceLimit = priceLimit;
        this.orderer = orderer;
        this.destination = destination;
        this.extendedAttributes = extendedAttributes;
        send();
    }
}
