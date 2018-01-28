package jp.co.myogadanimotors.myogadani.event.order;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderSender extends BaseEventSender<IAsyncOrderListener> {

    private long requestId;
    private long orderId;
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

    public OrderSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // event senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

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
        send(this::createNewOrder);
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
        send(this::createAmendOrder);
    }

    public void sendCancelOrder(long requestId, long orderId) {
        this.requestId = requestId;
        this.orderId = orderId;
        send(this::createCancelOrder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // event factory methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private IEvent createNewOrder(long eventId, long creationTime, IAsyncOrderListener asyncEventListener) {
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
                asyncEventListener
        );
    }

    private IEvent createAmendOrder(long eventId, long creationTime, IAsyncOrderListener asyncEventListener) {
        return new AmendOrder(
                eventId,
                creationTime,
                requestId,
                orderId,
                orderQuantity,
                priceLimit,
                extendedAttributes,
                asyncEventListener
        );
    }

    private IEvent createCancelOrder(long eventId, long creationTime, IAsyncOrderListener asyncEventListener) {
        return new CancelOrder(
                eventId,
                creationTime,
                requestId,
                orderId,
                asyncEventListener
        );
    }
}
