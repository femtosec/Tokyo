package jp.co.myogadanimotors.myogadani.ordermanagement.order;

import jp.co.myogadanimotors.myogadani.event.order.OrderSide;

import java.math.BigDecimal;
import java.util.Map;

public interface IOrder {
    long getOrderId();
    OrderState getOrderState();
    int getVersion();
    long getAccountId();
    String getSymbol();
    String getMic();
    String getOrderTag();
    OrderSide getOrderSide();
    BigDecimal getOrderQuantity();
    BigDecimal getExecQuantity();
    BigDecimal getCancelledQuantity();
    BigDecimal getExpiredQuantity();
    BigDecimal getRejectedQuantity();
    BigDecimal getExposedQuantity();
    default BigDecimal getRemainingQuantity() {
        return getOrderQuantity().subtract(getExecQuantity());
    }
    default BigDecimal getAvailableQuantity() {
        return getRemainingQuantity().subtract(getExposedQuantity());
    }
    BigDecimal getPriceLimit();
    String getExtendedAttribute(String key);
    Map<String, String> getExtendedAttributes();
}
