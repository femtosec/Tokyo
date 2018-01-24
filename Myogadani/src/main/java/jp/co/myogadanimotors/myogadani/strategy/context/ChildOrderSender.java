package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.common.OrderSide;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.RequestIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.IAsyncOrderListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.OrderSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.Orderer;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ChildOrderSender {

    private final OrderSender orderSeder;
    private final RequestIdGenerator requestIdGenerator;
    private final IOrder order;

    public ChildOrderSender(EventIdGenerator eventIdGenerator,
                            RequestIdGenerator requestIdGenerator,
                            ITimeSource timeSource,
                            IOrder order,
                            IAsyncOrderListener asyncOrderListener) {
        this.orderSeder = new OrderSender(eventIdGenerator, timeSource);
        this.requestIdGenerator = requestIdGenerator;
        this.order = new OrderView(order);

        orderSeder.addAsyncEventListener(asyncOrderListener);
    }

    public void sendExchangeNewOrder(String symbol,
                                     String mic,
                                     OrderSide orderSide,
                                     BigDecimal orderQuantity,
                                     BigDecimal priceLimit,
                                     String childOrderTag) {
        // todo: insert sanity check
        Map<String, String> extendedAttributes = new ConcurrentHashMap<>();
        extendedAttributes.put("childOrderTag", childOrderTag);
        orderSeder.sendNewOrder(
                requestIdGenerator.generateRequestId(),
                order.getOrderId(),
                order.getAccountId(),
                symbol,
                mic,
                orderSide,
                orderQuantity,
                priceLimit,
                Orderer.Strategy,
                OrderDestination.Exchange,
                extendedAttributes
        );
    }

    // todo: pass childOrderId instead of IOrder
    public void sendExchangeAmendOrder(IOrder childOrder,
                                       BigDecimal orderQuantity,
                                       BigDecimal priceLimit) {
        // todo: insert sanity check
        orderSeder.sendAmendOrder(
                requestIdGenerator.generateRequestId(),
                childOrder.getOrderId(),
                orderQuantity,
                priceLimit,
                childOrder.getExtendedAttributes()
        );
    }

    public void sendExchangeCancelOrder(long childOrderId) {
        // todo: insert sanity check
        orderSeder.sendCancelOrder(
                requestIdGenerator.generateRequestId(),
                childOrderId
        );
    }
}
