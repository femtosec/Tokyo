package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.kohinata.event.order.OrderSide;

import java.math.BigDecimal;

public interface IChildOrderSender {
    boolean sendExchangeNewOrder(String symbol,
                                 String mic,
                                 OrderSide orderSide,
                                 BigDecimal orderQuantity,
                                 BigDecimal priceLimit,
                                 String childOrderTag);

    boolean sendExchangeAmendOrder(long childOrderId,
                                   BigDecimal orderQuantity,
                                   BigDecimal priceLimit);

    boolean sendCancelOrder(long childOrderId);
}
