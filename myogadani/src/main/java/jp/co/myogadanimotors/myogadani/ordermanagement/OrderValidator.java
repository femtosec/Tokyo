package jp.co.myogadanimotors.myogadani.ordermanagement;

import jp.co.myogadanimotors.myogadani.eventprocessing.order.AmendOrder;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.CancelOrder;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.NewOrder;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.Order;
import jp.co.myogadanimotors.myogadani.store.master.extendedattriute.ExtendedAttributeMaster;
import jp.co.myogadanimotors.myogadani.store.master.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.master.product.ProductMaster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class OrderValidator {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final MarketMaster marketMaster;
    private final ProductMaster productMaster;
    private final ExtendedAttributeMaster extendedAttributeMaster;

    public OrderValidator(MarketMaster marketMaster, ProductMaster productMaster, ExtendedAttributeMaster extendedAttributeMaster) {
        this.marketMaster = requireNonNull(marketMaster);
        this.productMaster = requireNonNull(productMaster);
        this.extendedAttributeMaster = requireNonNull(extendedAttributeMaster);
    }

    public boolean isValidNewOrder(NewOrder newOrderEvent, Order newOrder) {
        if (productMaster.getBySymbol(newOrderEvent.getSymbol()) == null) {
            logger.warn("invalid symbol. (orderEvent: {})", newOrderEvent);
            return false;
        }

        if (marketMaster.getByMic(newOrderEvent.getMic()) == null) {
            logger.warn("invalid mic. (orderEvent: {})", newOrderEvent);
            return false;
        }

        if (newOrderEvent.getOrderSide() == null) {
            logger.warn("side is not set. (orderEvent: {})", newOrderEvent);
            return false;
        }

        if (newOrderEvent.getOrderer() == null) {
            logger.warn("orderer is not set. (orderEvent: {})", newOrderEvent);
            return false;
        }

        if (newOrderEvent.getDestination() == null) {
            logger.warn("destination is not set. (orderEvent: {})", newOrderEvent);
            return false;
        }

        if (newOrderEvent.getRequestId() < 0) {
            logger.warn("request id is not set. (orderEvent: {})", newOrderEvent);
            return false;
        }

        if (newOrder.isStrategyOrder()) {
            if (newOrder.getStrategy() == null) {
                logger.warn("strategy is null despite strategy order. (isStrategyOrder: {})", newOrder.isStrategyOrder());
                return false;
            }
        } else {
            if (newOrder.getStrategy() != null) {
                logger.warn("strategy is not null despite not strategy order. (isStrategyOrder: {})", newOrder.isStrategyOrder());
                return false;
            }
        }

        return isValidExtendedAttributes(newOrderEvent.getExtendedAttributes());
    }

    public boolean isValidAmendOrder(AmendOrder amendOrderEvent, Order currentOrder) {
        if (!currentOrder.getOrderState().isAmendable()) {
            logger.warn("order is not amendable. (orderState: {})", currentOrder.getOrderState());
            return false;
        }

        if (amendOrderEvent.getRequestId() < 0) {
            logger.warn("request id is not set. (amendOrderEvent: {})", amendOrderEvent);
            return false;
        }

        return isValidExtendedAttributes(amendOrderEvent.getExtendedAttributes());
    }

    public boolean isValidCancelOrder(CancelOrder cancelOrderEvent, Order currentOrder) {
        if (!currentOrder.getOrderState().isCancellable()) {
            logger.warn("order is not cancellable. (orderState: {})", currentOrder.getOrderState());
            return false;
        }

        if (cancelOrderEvent.getRequestId() < 0) {
            logger.warn("invalid cancel order event. (cancelOrderEvent: {})", cancelOrderEvent);
            return false;
        }

        return true;
    }

    private boolean isValidExtendedAttributes(Map<String, String> extendedAttributes) {
        for (String key : extendedAttributes.keySet()) {
            if (extendedAttributeMaster.getByName(key) == null) {
                logger.warn("invalid extended attribute (extendedAttributeName: {})", key);
                return false;
            }
        }

        return true;
    }
}
