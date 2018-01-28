package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.myogadani.event.RequestIdGenerator;
import jp.co.myogadanimotors.myogadani.event.order.*;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ChildOrderSender implements IChildOrderSender {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final OrderSender orderSeder;
    private final RequestIdGenerator requestIdGenerator;
    private final ChildOrderContainer childOrderContainer;
    private final IOrder order;

    public ChildOrderSender(EventIdGenerator eventIdGenerator,
                            RequestIdGenerator requestIdGenerator,
                            ITimeSource timeSource,
                            ChildOrderContainer childOrderContainer,
                            IOrder order,
                            IAsyncOrderListener asyncOrderListener) {
        this.orderSeder = new OrderSender(eventIdGenerator, timeSource);
        this.requestIdGenerator = requestIdGenerator;
        this.childOrderContainer = childOrderContainer;
        this.order = new OrderView(order);

        orderSeder.addAsyncEventListener(asyncOrderListener);
    }

    @Override
    public boolean sendExchangeNewOrder(String symbol,
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

        childOrderContainer.incrementOnTheWireOrdersCount();

        return true;
    }

    @Override
    public boolean sendExchangeAmendOrder(long childOrderId,
                                          BigDecimal orderQuantity,
                                          BigDecimal priceLimit) {
        IOrder childOrder = childOrderContainer.getChildOrder(childOrderId);
        if (childOrder == null) {
            logger.warn("child order doesn't exist. (childOrderId: {})", childOrderId);
            return false;
        }

        orderSeder.sendAmendOrder(
                requestIdGenerator.generateRequestId(),
                childOrder.getOrderId(),
                orderQuantity,
                priceLimit,
                childOrder.getExtendedAttributes()
        );

        childOrderContainer.incrementOnTheWireOrdersCount();

        return true;
    }

    @Override
    public boolean sendCancelOrder(long childOrderId) {
        if (!childOrderContainer.contains(childOrderId)) {
            logger.warn("child order doesn't exist. (childOrderId: {})", childOrderId);
            return false;
        }

        orderSeder.sendCancelOrder(
                requestIdGenerator.generateRequestId(),
                childOrderId
        );

        childOrderContainer.incrementOnTheWireOrdersCount();

        return true;
    }
}
