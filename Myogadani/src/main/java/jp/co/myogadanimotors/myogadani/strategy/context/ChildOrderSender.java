package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.common.OrderSide;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.OrderSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.Orderer;
import jp.co.myogadanimotors.myogadani.idgenerator.IIdGenerator;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.StrategyMaster;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.math.BigDecimal;

public class ChildOrderSender {

    private final OrderSender orderSeder;
    private final IIdGenerator requestIdGenerator;
    private final StrategyMaster strategyMaster;
    private final IOrder order;

    public ChildOrderSender(EventIdGenerator eventIdGenerator,
                            ITimeSource timeSource,
                            IIdGenerator requestIdGenerator,
                            StrategyMaster strategyMaster,
                            IOrder order) {
        this.orderSeder = new OrderSender(eventIdGenerator, timeSource);
        this.requestIdGenerator = requestIdGenerator;
        this.strategyMaster = strategyMaster;
        this.order = new OrderView(order);
    }

    public void sendExchangeNewOrder(String symbol,
                                String mic,
                                OrderSide orderSide,
                                BigDecimal orderQuantity,
                                BigDecimal priceLimit) {
        // todo: insert sanity check
        orderSeder.sendNewOrder(
                requestIdGenerator.generateId(),
                order.getOrderId(),
                order.getAccountId(),
                symbol,
                mic,
                orderSide,
                orderQuantity,
                priceLimit,
                Orderer.Strategy,
                OrderDestination.Exchange,
                null
        );
    }

    public void sendExchangeAmendOrder(long childOrderId,
                                  BigDecimal orderQuantity,
                                  BigDecimal priceLimit) {
        // todo: insert sanity check
        orderSeder.sendAmendOrder(
                requestIdGenerator.generateId(),
                childOrderId,
                orderQuantity,
                priceLimit,
                null
        );
    }

    public void sendExchangeCancelOrder(long childOrderId) {
        // todo: insert sanity check
        orderSeder.sendCancelOrder(
                requestIdGenerator.generateId(),
                childOrderId
        );
    }
}
