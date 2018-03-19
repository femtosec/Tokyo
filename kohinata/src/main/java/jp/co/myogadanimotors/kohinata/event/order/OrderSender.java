package jp.co.myogadanimotors.kohinata.event.order;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

import java.math.BigDecimal;
import java.util.Map;

public class OrderSender extends BaseEventSender<IAsyncOrderListener> {

    public OrderSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
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
        send((eventId, creationTime, asyncEventListener) ->
                new NewOrder(
                        eventId,
                        creationTime,
                        asyncEventListener,
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
                        extendedAttributes
                )
        );
    }

    public void sendAmendOrder(long requestId,
                               long orderId,
                               BigDecimal orderQuantity,
                               BigDecimal priceLimit,
                               Map<String, String> extendedAttributes) {
        send((eventId, creationTime, asyncEventListener) ->
                new AmendOrder(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderId,
                        orderQuantity,
                        priceLimit,
                        extendedAttributes
                )
        );
    }

    public void sendCancelOrder(long requestId, long orderId) {
        send((eventId, creationTime, asyncEventListener) ->
                new CancelOrder(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderId
                )
        );
    }
}
