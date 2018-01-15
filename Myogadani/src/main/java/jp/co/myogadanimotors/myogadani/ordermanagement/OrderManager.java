package jp.co.myogadanimotors.myogadani.ordermanagement;

import jp.co.myogadanimotors.myogadani.common.Constants;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventRelay;
import jp.co.myogadanimotors.myogadani.eventprocessing.fill.FillEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.fill.IFillEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.IOrderEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IReportEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.ReportEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.strategyinvoker.StrategyInvokerSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.timerevent.ITimerEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timerevent.TimerEvent;
import jp.co.myogadanimotors.myogadani.idgenerator.IIdGenerator;
import jp.co.myogadanimotors.myogadani.idgenerator.IdGenerator;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.Order;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.OrderState;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.IStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.IStrategyEvent;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder.*;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorderfill.StrategyChildOrderFill;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.order.*;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.timer.StrategyTimerEvent;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OrderManager implements IOrderEventListener, IFillEventListener, IReportEventListener, ITimerEventListener {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final IEventRelay emsReportRelay;
    private final IEventRelay exchangeOrderRelay;
    private final StrategyInvokerSender[] strategyInvokerSenders;
    private final IIdGenerator orderIdGenerator = new IdGenerator(0L);
    private final IStrategyFactory strategyFactory;
    private final ITimeSource timeSource;
    private final Map<Long, Order> ordersByOrderId = new ConcurrentHashMap<>();
    private final Map<Long, Order> amendOrdersByRequestId = new ConcurrentHashMap<>();
    private final Map<Long, Order> terminatedOrdersByOrderId = new ConcurrentHashMap<>();

    private int lastThreadId = 0;

    public OrderManager(IEventRelay emsReportRelay,
                        IEventRelay exchangeOrderRelay,
                        IStrategyFactory strategyFactory,
                        ITimeSource timeSource,
                        StrategyInvokerSender... strategyInvokerSenders) {
        this.emsReportRelay = emsReportRelay;
        this.exchangeOrderRelay = exchangeOrderRelay;
        this.strategyFactory = strategyFactory;
        this.timeSource = timeSource;
        this.strategyInvokerSenders = strategyInvokerSenders;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // transaction senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendStrategyInvoker(Order order, IStrategyEvent strategyEvent) {
        StrategyInvokerSender strategyInvokerSender = strategyInvokerSenders[order.getThreadId()];
        strategyInvokerSender.sendStrategyInvoker(strategyEvent, order.getStrategy());
    }

    private void sendExchangeOrder(OrderEvent orderEvent) {
        exchangeOrderRelay.relay(orderEvent);
    }

    private void sendEmsReport(ReportEvent reportEvent) {
        emsReportRelay.relay(reportEvent);
    }

    private void sendEmsFill(FillEvent fillEvent) {
        emsReportRelay.relay(fillEvent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // order event processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void processOrderNew(OrderEvent orderEvent) {
        // create order object
        Order newOrder = createNewOrder(orderEvent);

        // sanity check
        if (!isValidNewOrderEvent(orderEvent, newOrder)) {
            // todo: send reject to orderer
            logger.warn("invalid order event. (orderEvent: {})", orderEvent);
            return;
        }

        // put order to map
        put(newOrder);

        // send order to destination
        if (newOrder.getDestination() == OrderDestination.Strategy) {
            // if strategy order, send strategy invoker
            IStrategyEvent strategyEvent = new StrategyNew(
                    orderEvent.getRequestId(),
                    new OrderView(newOrder),
                    orderEvent.getOrderer(),
                    orderEvent.getDestination()
            );
            sendStrategyInvoker(newOrder, strategyEvent);
        } else {
            // if exchange order, send to exchange
            sendExchangeOrder(orderEvent);
        }
    }

    @Override
    public void processOrderAmend(OrderEvent orderEvent) {
        // find order object by order id and check order state
        Order currentOrder = findAndCheckOrder(orderEvent.getOrderId());
        if (currentOrder == null) {
            // todo: send reject to orderer
            logger.warn("stop processing. (orderEvent: {})", orderEvent);
            return;
        }

        // sanity check
        if (!isValidAmendOrderEvent(orderEvent, currentOrder)) {
            // todo: send reject to orderer
            logger.warn("invalid amend order event. (amendOrderEvent: {})", orderEvent);
            return;
        }

        // order state check
        if (!currentOrder.getOrderState().isAmendable()) {
            // todo: send reject to orderer
            logger.info("amend order not acceptable. (orderState: {})", currentOrder.getOrderState());
            return;
        }

        // change order state
        currentOrder.setOrderState(OrderState.Amend);

        // create amend order object which contains new parameters
        Order amendOrder = createAmendOrder(orderEvent, currentOrder);
        amendOrdersByRequestId.put(orderEvent.getRequestId(), amendOrder);

        // send order to destination
        if (amendOrder.getDestination() == OrderDestination.Strategy) {
            // if strategy order, send strategy invoker
            IStrategyEvent strategyEvent = new StrategyAmend(
                    orderEvent.getRequestId(),
                    new OrderView(currentOrder),
                    new OrderView(amendOrder),
                    orderEvent.getOrderer(),
                    orderEvent.getDestination()
            );
            sendStrategyInvoker(currentOrder, strategyEvent);
        } else {
            // if exchange order, send to exchange
            sendExchangeOrder(orderEvent);
        }
    }

    @Override
    public void processOrderCancel(OrderEvent orderEvent) {
        // find order object by order id and check order state
        Order currentOrder = findAndCheckOrder(orderEvent.getOrderId());
        if (currentOrder == null) {
            logger.warn("stop processing. (orderEvent: {})", orderEvent);
            // todo: send reject to orderer
            return;
        }

        // sanity check
        if (!isValidCancelOrderEvent(orderEvent, currentOrder)) {
            // todo: send reject to orderer
            logger.warn("invalid cancel order event. (cancelOrderEvent: {})", orderEvent);
            return;
        }

        // order state check
        if (!currentOrder.getOrderState().isCancellable()) {
            // todo: send reject to orderer
            logger.info("cancel order not acceptable. (orderState: {})", currentOrder.getOrderState());
            return;
        }

        // change order state
        currentOrder.setOrderState(OrderState.Cancel);

        // send order to destination
        if (currentOrder.getDestination() == OrderDestination.Strategy) {
            // if strategy order, send strategy invoker
            IStrategyEvent strategyEvent = new StrategyCancel(
                    orderEvent.getRequestId(),
                    new OrderView(currentOrder),
                    orderEvent.getOrderer(),
                    orderEvent.getDestination()
            );
            sendStrategyInvoker(currentOrder, strategyEvent);
        } else {
            // if exchange order, send to exchange
            sendExchangeOrder(orderEvent);
        }
    }

    private boolean isValidNewOrderEvent(OrderEvent orderEvent, Order newOrder) {
        if (orderEvent.getSymbol() == null) {
            logger.warn("symbol is not set. (orderEvent: {})", orderEvent);
            return false;
        }

        if (orderEvent.getMic() == null) {
            logger.warn("mic is not set. (orderEvent: {})", orderEvent);
            return false;
        }

        if (orderEvent.getOrderSide() == null) {
            logger.warn("side is not set. (orderEvent: {})", orderEvent);
            return false;
        }

        if (orderEvent.getOrderer() == null) {
            logger.warn("orderer is not set. (orderEvent: {})", orderEvent);
            return false;
        }

        if (orderEvent.getDestination() == null) {
            logger.warn("destination is not set. (orderEvent: {})", orderEvent);
            return false;
        }

        if (orderEvent.getRequestId() < 0) {
            logger.warn("request id is not set. (orderEvent: {})", orderEvent);
            return false;
        }

        if (newOrder.isStrategyOrder() && newOrder.getStrategy() == null) {
            logger.warn("strategy type invalid.");
            return false;
        }

        return true;
    }

    private boolean isValidAmendOrderEvent(OrderEvent amendOrderEvent, Order currentOrder) {
        if (amendOrderEvent.getRequestId() < 0) {
            logger.warn("request id is not set. (amendOrderEvent: {})", amendOrderEvent);
            return false;
        }

        if (!currentOrder.getSymbol().equals(amendOrderEvent.getSymbol())) {
            logger.warn("symbol cannot be amended. (amendOrderEvent: {}, currentOrder: {})", amendOrderEvent, currentOrder);
            return false;
        }

        if (currentOrder.getAccountId() != amendOrderEvent.getAccountId()) {
            logger.warn("account id cannot be amended. (amendOrderEvent: {}, currentOrder: {})", amendOrderEvent, currentOrder);
            return false;
        }

        if (currentOrder.getOrderSide() != amendOrderEvent.getOrderSide()) {
            logger.warn("side cannot be amended. (amendOrderEvent: {}, currentOrder: {})", amendOrderEvent, currentOrder);
            return false;
        }

        if (!currentOrder.getMic().equals(amendOrderEvent.getMic())) {
            logger.warn("mic cannot be amended. (amendOrderEvent: {}, currentOrder: {})", amendOrderEvent, currentOrder);
            return false;
        }

        if (currentOrder.getOrderer() != amendOrderEvent.getOrderer()) {
            logger.warn("orderer cannot be amended. (amendOrderEvent: {}, currentOrder: {})", amendOrderEvent, currentOrder);
            return false;
        }

        if (currentOrder.getDestination() != amendOrderEvent.getDestination()) {
            logger.warn("destination cannot be amended. (amendOrderEvent: {}, currentOrder: {})", amendOrderEvent, currentOrder);
            return false;
        }

        return true;
    }

    private boolean isValidCancelOrderEvent(OrderEvent cancelOrderEvent, Order currentOrder) {
        if (cancelOrderEvent.getRequestId() < 0) {
            logger.warn("invalid cancel order event. (cancelOrderEvent: {})", cancelOrderEvent);
            return false;
        }

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // report event processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void processReportNewAck(ReportEvent reportEvent) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(reportEvent.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", reportEvent);
            return;
        }

        // update stored order
        order.setOrderState(OrderState.NewAck);

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", reportEvent);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExposedQuantity(parentStrategyOrder.getExposedQuantity().add(order.getOrderQuantity()));

            // return child order report to parent strategy
            IStrategyEvent strategyEvent = new StrategyChildOrderNewAck(new OrderView(parentStrategyOrder), new OrderView(order), reportEvent.getMessage());
            sendStrategyInvoker(parentStrategyOrder, strategyEvent);
        } else {
            // send report event to ems
            sendEmsReport(reportEvent);
        }

        // return report event to strategy if order is a strategy order
        if (order.isStrategyOrder()) {
            IStrategyEvent strategyEvent = new StrategyNewAck(new OrderView(order));
            sendStrategyInvoker(order, strategyEvent);
        }
    }

    @Override
    public void processReportNewReject(ReportEvent reportEvent) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(reportEvent.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", reportEvent);
            return;
        }

        // update stored order
        order.setRejectedQuantity(order.getOrderQuantity());
        order.setOrderState(OrderState.NewReject);

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", reportEvent);
                return;
            }

            // return child order report to parent strategy
            IStrategyEvent strategyEvent = new StrategyChildOrderNewReject(new OrderView(parentStrategyOrder), new OrderView(order), reportEvent.getMessage());
            sendStrategyInvoker(parentStrategyOrder, strategyEvent);
        } else {
            // send report event to ems
            sendEmsReport(reportEvent);
        }

        // return report event to strategy if the order is a strategy order
        if (order.isStrategyOrder()) {
            IStrategyEvent strategyEvent = new StrategyNewReject(new OrderView(order));
            sendStrategyInvoker(order, strategyEvent);
        }

        // move order to terminated order list
        moveOrderToTerminatedOrderList(order);
    }

    @Override
    public void processReportAmendAck(ReportEvent reportEvent) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(reportEvent.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", reportEvent);
            return;
        }

        // find stored amend order by request id
        Order amendOrder = amendOrdersByRequestId.get(reportEvent.getRequestId());
        if (amendOrder == null) {
            logger.warn("amend order not found. ({})", reportEvent);
            return;
        }

        // update stored order
        BigDecimal orderQtyDiff = amendOrder.getOrderQuantity().subtract(order.getOrderQuantity());
        order.setCancelledQuantity(order.getCancelledQuantity().subtract(orderQtyDiff));
        order.setOrderQuantity(amendOrder.getOrderQuantity());
        order.setPriceLimit(amendOrder.getPriceLimit());
        order.setExtendedAttributes(amendOrder.getExtendedAttributes());
        order.incrementVersion();
        if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
            order.setOrderState(OrderState.CancelAck);
        } else {
            order.setOrderState(OrderState.AmendAck);
        }

        // remove stored amend order
        amendOrdersByRequestId.remove(reportEvent.getRequestId());

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", reportEvent);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExposedQuantity(parentStrategyOrder.getExposedQuantity().add(orderQtyDiff));

            // return child order report to parent strategy
            IStrategyEvent strategyEvent = new StrategyChildOrderAmendAck(new OrderView(parentStrategyOrder), new OrderView(order), reportEvent.getMessage());
            sendStrategyInvoker(parentStrategyOrder, strategyEvent);
        } else {
            // send report event to ems
            sendEmsReport(reportEvent);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IStrategyEvent strategyEvent = new StrategyAmendAck(new OrderView(order));
            sendStrategyInvoker(order, strategyEvent);
        }
    }

    @Override
    public void processReportAmendReject(ReportEvent reportEvent) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(reportEvent.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", reportEvent);
            return;
        }

        // find stored amend order by request id
        Order amendOrder = amendOrdersByRequestId.get(reportEvent.getRequestId());
        if (amendOrder == null) {
            logger.warn("amend order not found. ({})", reportEvent);
            return;
        }

        // update stored order
        order.setOrderState(OrderState.AmendReject);

        // remove stored amend order
        amendOrdersByRequestId.remove(reportEvent.getRequestId());

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", reportEvent);
                return;
            }

            // return child order report to parent strategy
            IStrategyEvent strategyEvent = new StrategyChildOrderAmendReject(new OrderView(parentStrategyOrder), new OrderView(order), reportEvent.getMessage());
            sendStrategyInvoker(parentStrategyOrder, strategyEvent);
        } else {
            // send report event to ems
            sendEmsReport(reportEvent);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IStrategyEvent strategyEvent = new StrategyAmendReject(new OrderView(order));
            sendStrategyInvoker(order, strategyEvent);
        }
    }

    @Override
    public void processReportCancelAck(ReportEvent reportEvent) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(reportEvent.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", reportEvent);
            return;
        }

        // update stored order
        BigDecimal orderQtyDiff = BigDecimal.ZERO.subtract(order.getOrderQuantity());
        order.setCancelledQuantity(order.getCancelledQuantity().subtract(orderQtyDiff));
        order.setOrderQuantity(BigDecimal.ZERO);
        order.incrementVersion();
        order.setOrderState(OrderState.CancelAck);

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", reportEvent);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExposedQuantity(parentStrategyOrder.getExposedQuantity().add(orderQtyDiff));

            // return child order report to parent strategy
            IStrategyEvent strategyEvent = new StrategyChildOrderCancelAck(new OrderView(parentStrategyOrder), new OrderView(order), reportEvent.getMessage());
            sendStrategyInvoker(parentStrategyOrder, strategyEvent);
        } else {
            // send report event to ems
            sendEmsReport(reportEvent);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IStrategyEvent strategyEvent = new StrategyCancelAck(new OrderView(order));
            sendStrategyInvoker(order, strategyEvent);
        }

        // mover order to terminated order list
        moveOrderToTerminatedOrderList(order);
    }

    @Override
    public void processReportCancelReject(ReportEvent reportEvent) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(reportEvent.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", reportEvent);
            return;
        }

        // update stored order
        order.setOrderState(OrderState.CancelReject);

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", reportEvent);
                return;
            }

            // return child order report to parent strategy
            IStrategyEvent strategyEvent = new StrategyChildOrderCancelReject(new OrderView(parentStrategyOrder), new OrderView(order), reportEvent.getMessage());
            sendStrategyInvoker(parentStrategyOrder, strategyEvent);
        } else {
            // send report event to ems
            sendEmsReport(reportEvent);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IStrategyEvent strategyEvent = new StrategyCancelReject(new OrderView(order));
            sendStrategyInvoker(order, strategyEvent);
        }
    }

    @Override
    public void processReportUnsolicitedCancel(ReportEvent reportEvent) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(reportEvent.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", reportEvent);
            return;
        }

        // update stored order
        BigDecimal orderQtyDiff = BigDecimal.ZERO.subtract(order.getOrderQuantity());
        order.setCancelledQuantity(order.getCancelledQuantity().subtract(orderQtyDiff));
        order.setOrderQuantity(BigDecimal.ZERO);
        order.incrementVersion();
        order.setOrderState(OrderState.UnsolicitedCancel);

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", reportEvent);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExposedQuantity(parentStrategyOrder.getExposedQuantity().add(orderQtyDiff));

            // return child order report to parent strategy
            IStrategyEvent strategyEvent = new StrategyChildOrderUnsolicitedCancel(new OrderView(parentStrategyOrder), new OrderView(order), reportEvent.getMessage());
            sendStrategyInvoker(parentStrategyOrder, strategyEvent);
        } else {
            // send report event to ems
            sendEmsReport(reportEvent);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IStrategyEvent strategyEvent = new StrategyUnsolicitedCancel(new OrderView(order));
            sendStrategyInvoker(order, strategyEvent);
        }

        // mover order to terminated order list
        moveOrderToTerminatedOrderList(order);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // fill event processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void processFillEvent(FillEvent fillEvent) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(fillEvent.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (fillEvent: {})", fillEvent);
            return;
        }

        // update stored order
        order.setExecQuantity(order.getExecQuantity().add(fillEvent.getExecQuantity()));
        if (order.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
            order.setOrderState(OrderState.FullyFilled);
        }

        // send fill to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", fillEvent);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExecQuantity(parentStrategyOrder.getExecQuantity().add(fillEvent.getExecQuantity()));
            if (parentStrategyOrder.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {
                parentStrategyOrder.setOrderState(OrderState.FullyFilled);
            }

            // return child order report to parent strategy
            IStrategyEvent strategyEvent = new StrategyChildOrderFill(new OrderView(parentStrategyOrder), new OrderView(order));
            sendStrategyInvoker(parentStrategyOrder, strategyEvent);
        } else {
            // send fill event to ems
            sendEmsFill(fillEvent);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // timer event processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void processTimerEvent(TimerEvent timerEvent) {
        Order order = get(timerEvent.getOrderId());
        if (order == null) {
            logger.warn("order not found. ({})", timerEvent);
        }

        if (order.isStrategyOrder()) {
            IStrategyEvent strategyEvent = new StrategyTimerEvent(timerEvent.getUserTag(), timerEvent.getTimerEventTime());
            sendStrategyInvoker(order, strategyEvent);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // utilities
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private int getStrategyTypeId(OrderEvent orderEvent) {
        String strId = orderEvent.getExtendedAttributes().get("strategyTypeId");
        if (strId != null) {
            return Integer.parseInt(strId);
        }

        return Integer.MIN_VALUE;
    }

    private Order createNewOrder(OrderEvent orderEvent) {
        Order parentStrategyOrder = null;
        if (orderEvent.getParentOrderId() != Constants.NOT_SET_ID_LONG) {
            parentStrategyOrder = get(orderEvent.getParentOrderId());
        }

        Order newOrder = createOrder(orderEvent, orderIdGenerator.generateId(), parentStrategyOrder, (orderEvent.getDestination() == OrderDestination.Strategy));

        if (newOrder.isStrategyOrder()) {
            newOrder.setStrategy(strategyFactory.create(getStrategyTypeId(orderEvent), newOrder));
        }

        return newOrder;
    }

    private Order createAmendOrder(OrderEvent orderEvent, Order currentOrder) {
        Order amendOrder = createOrder(orderEvent, currentOrder.getOrderId(), currentOrder.getParentStrategyOrder(), currentOrder.isStrategyOrder());

        if (amendOrder.isStrategyOrder()) {
            // if strategy type amend, create new strategy
            IStrategy currentStrategy = currentOrder.getStrategy();
            int newStrategyTypeId = getStrategyTypeId(orderEvent);
            if (currentStrategy.getStrategyTypeId() != newStrategyTypeId) {
                amendOrder.setStrategy(strategyFactory.create(newStrategyTypeId, amendOrder));
            } else {
                amendOrder.setStrategy(currentStrategy);
            }
        }

        return amendOrder;
    }

    private Order createOrder(OrderEvent orderEvent, long orderId, Order parentStrategyOrder, boolean isStrategyOrder) {
        return new Order(
                orderId,
                orderEvent.getAccountId(),
                orderEvent.getSymbol(),
                orderEvent.getMic(),
                orderEvent.getOrderSide(),
                orderEvent.getOrderQuantity(),
                orderEvent.getPriceLimit(),
                orderEvent.getOrderer(),
                orderEvent.getDestination(),
                orderEvent.getExtendedAttributes(),
                parentStrategyOrder,
                getThreadId(isStrategyOrder, parentStrategyOrder)
        );
    }

    private int getThreadId(boolean isStrategyOrder, Order parentStrategyOrder) {
        if (!isStrategyOrder) return Constants.NOT_SET_ID_INT;

        if (parentStrategyOrder != null) return parentStrategyOrder.getThreadId();

        lastThreadId++;
        if (strategyInvokerSenders.length == lastThreadId) {
            lastThreadId = 0;
        }

        return lastThreadId;
    }

    /**
     * find Order by orderId and check whether the order state is terminal one or not
     */
    private Order findAndCheckOrder(long orderId) {
        Order order = get(orderId);
        if (order == null) {
            logger.warn("order not found. (orderId: {})", orderId);
            return null;
        }

        // check order state
        if (order.getOrderState().isTerminalState()) {
            logger.info("order already terminated. (orderId: {})", orderId);
            return null;
        }

        return order;
    }

    private Order get(long orderId) {
        return ordersByOrderId.get(orderId);
    }

    private void put(Order order) {
        ordersByOrderId.put(order.getOrderId(), order);
    }

    private void moveOrderToTerminatedOrderList(Order order) {
        if (!order.getOrderState().isTerminalState()) {
            logger.warn("order still working. (order: {})", order);
            return;
        }

        ordersByOrderId.remove(order.getOrderId());
        terminatedOrdersByOrderId.put(order.getOrderId(), order);
    }
}
