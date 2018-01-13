package jp.co.myogadanimotors.myogadani.eventprocessing.orderevent;

import jp.co.myogadanimotors.myogadani.common.Constants;
import jp.co.myogadanimotors.myogadani.common.OrderSide;
import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventStream;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.math.BigDecimal;
import java.util.Map;

public final class OrderSender extends AbstractEventSender<OrderEvent> {

    private final RequestIdGenerator requestIdGenerator;
    private OrderType orderType;
    private long orderId;
    private long parentOrderId;
    private long requestId;
    private long accountId;
    private String symbol;
    private String mic;
    private OrderSide orderSide;
    private BigDecimal orderQuantity;
    private BigDecimal priceLimit;
    private Orderer orderer;
    private OrderDestination destination;
    private Map<String, String> extendedAttributes;

    public OrderSender(String eventSenderName,
                       IEventIdGenerator idGenerator,
                       ITimeSource timeSource,
                       RequestIdGenerator requestIdGenerator,
                       IEventStream... eventStreams) {
        super(eventSenderName, idGenerator, timeSource, eventStreams);
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    protected final OrderEvent createEvent() {
        return new OrderEvent(
                generateEventId(),
                getCurrentTime(),
                getEventSenderName(),
                orderType,
                orderId,
                parentOrderId,
                requestId,
                accountId,
                symbol,
                mic,
                orderSide,
                orderQuantity,
                priceLimit,
                orderer,
                destination,
                extendedAttributes
        );
    }

    public final void sendNew(long parentOrderId,
                              long accountId,
                              String symbol,
                              String mic,
                              OrderSide orderSide,
                              BigDecimal orderQuantity,
                              BigDecimal priceLimit,
                              Orderer orderer,
                              OrderDestination destination,
                              Map<String, String> extendedAttributes) {
        sendOrderRequest(
                OrderType.New,
                Constants.NOT_SET_ID_LONG,
                parentOrderId,
                accountId,
                symbol,
                mic,
                orderSide,
                orderQuantity,
                priceLimit,
                orderer,
                destination,
                extendedAttributes
        );
    }

    public final void sendAmend(long orderId,
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
        sendOrderRequest(
                OrderType.Amend,
                orderId,
                parentOrderId,
                accountId,
                symbol,
                mic,
                orderSide,
                orderQuantity,
                priceLimit,
                orderer,
                destination,
                extendedAttributes
        );
    }

    public final void sendCancel(long orderId,
                                 long parentOrderId,
                                 Orderer orderer,
                                 OrderDestination orderDestination) {
        sendOrderRequest(
                OrderType.Cancel,
                orderId,
                parentOrderId,
                Constants.NOT_SET_ID_LONG,
                null,
                null,
                null,
                null,
                null,
                orderer,
                orderDestination,
                null
        );
    }

    private void sendOrderRequest(OrderType orderType,
                                  long orderId,
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
        this.orderType = orderType;
        this.orderId = orderId;
        this.parentOrderId = parentOrderId;
        this.requestId = requestIdGenerator.generateRequestId();
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
