package jp.co.myogadanimotors.myogadani.ordermanagement;

import emsadapter.IEmsAdapter;
import exchangeadapter.IExchangeAdapter;
import jp.co.myogadanimotors.myogadani.common.Constants;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.*;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.*;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.IAsyncTimerEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.TimerEvent;
import jp.co.myogadanimotors.myogadani.idgenerator.IIdGenerator;
import jp.co.myogadanimotors.myogadani.idgenerator.IdGenerator;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.Order;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.OrderState;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.IStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
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
import java.util.concurrent.Executor;
import java.util.function.Function;

public final class OrderManager implements IAsyncOrderListener, IAsyncReportListener, IAsyncFillListener, IAsyncTimerEventListener {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final ReportSender emsReportSender;
    private final FillSender emsFillSender;
    private final OrderSender exchangeOrderSender;
    private final EventIdGenerator eventIdGenerator;
    private final IIdGenerator orderIdGenerator = new IdGenerator(0L);
    private final ITimeSource timeSource;
    private final IStrategyFactory strategyFactory;
    private final Executor eventExecutor;
    private final Executor[] strategyEventExecutors;
    private final Map<Long, Order> ordersByOrderId = new ConcurrentHashMap<>();
    private final Map<Long, Order> amendOrdersByRequestId = new ConcurrentHashMap<>();
    private final Map<Long, Order> terminatedOrdersByOrderId = new ConcurrentHashMap<>();

    private int lastThreadId = 0;

    public OrderManager(IEmsAdapter emsAdapter,
                        IExchangeAdapter exchangeAdapter,
                        EventIdGenerator eventIdGenerator,
                        ITimeSource timeSource,
                        IStrategyFactory strategyFactory,
                        Executor eventExecutor,
                        Executor... strategyEventExecutors) {
        this.emsReportSender = new ReportSender(eventIdGenerator, timeSource);
        this.emsFillSender = new FillSender(eventIdGenerator, timeSource);
        this.exchangeOrderSender = new OrderSender(eventIdGenerator, timeSource);
        this.eventIdGenerator = eventIdGenerator;
        this.timeSource = timeSource;
        this.strategyFactory = strategyFactory;
        this.eventExecutor = eventExecutor;
        this.strategyEventExecutors = strategyEventExecutors;

        emsReportSender.addAsyncEventListener(emsAdapter);
        emsFillSender.addAsyncEventListener(emsAdapter);
        exchangeOrderSender.addAsyncEventListener(exchangeAdapter);

        emsAdapter.addEventListener(this);
        exchangeAdapter.addReportListener(this);
        exchangeAdapter.addFillListener(this);
    }

    @Override
    public Executor getExecutor() {
        return eventExecutor;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // transaction senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendStrategyEvent(int threadId, IEvent strategyEvent) {
        Executor strategyEventExecutor = strategyEventExecutors[threadId];
        strategyEventExecutor.execute(strategyEvent);
    }

    private void sendExchangeNewOrder(NewOrder newOrderEvent) {
        exchangeOrderSender.sendNewOrder(
                newOrderEvent.getRequestId(),
                newOrderEvent.getParentOrderId(),
                newOrderEvent.getAccountId(),
                newOrderEvent.getSymbol(),
                newOrderEvent.getMic(),
                newOrderEvent.getOrderSide(),
                newOrderEvent.getOrderQuantity(),
                newOrderEvent.getPriceLimit(),
                newOrderEvent.getOrderer(),
                newOrderEvent.getDestination(),
                newOrderEvent.getExtendedAttributes()
        );
    }

    private void sendExchangeAmendOrder(AmendOrder amendOrderEvent) {
        exchangeOrderSender.sendAmendOrder(
                amendOrderEvent.getRequestId(),
                amendOrderEvent.getOrderId(),
                amendOrderEvent.getOrderQuantity(),
                amendOrderEvent.getPriceLimit(),
                amendOrderEvent.getExtendedAttributes()
        );
    }

    private void sendExchangeCancelOrder(CancelOrder cancelOrderEvent) {
        exchangeOrderSender.sendCancelOrder(
                cancelOrderEvent.getRequestId(),
                cancelOrderEvent.getOrderId()
        );
    }

    private void sendEmsNewAck(NewAck newAckEvent) {
        emsReportSender.sendNewAck(newAckEvent.getRequestId(), newAckEvent.getOrderId());
    }

    private void sendEmsNewReject(NewReject newRejectEvent) {
        emsReportSender.sendNewReject(newRejectEvent.getRequestId(), newRejectEvent.getOrderId(), newRejectEvent.getMessage());
    }

    private void sendEmsAmendAck(AmendAck amendAckEvent) {
        emsReportSender.sendAmendAck(amendAckEvent.getRequestId(), amendAckEvent.getOrderId());
    }

    private void sendEmsAmendReject(AmendReject amendRejectEvent) {
        emsReportSender.sendAmendReject(amendRejectEvent.getRequestId(), amendRejectEvent.getOrderId(), amendRejectEvent.getMessage());
    }

    private void sendEmsCancelAck(CancelAck cancelAckEvent) {
        emsReportSender.sendCancelAck(cancelAckEvent.getRequestId(), cancelAckEvent.getOrderId());
    }

    private void sendEmsCancelReject(CancelReject cancelRejectEvent) {
        emsReportSender.sendCancelReject(cancelRejectEvent.getRequestId(), cancelRejectEvent.getOrderId(), cancelRejectEvent.getMessage());
    }

    private void sendEmsUnsolicitedCancel(UnsolicitedCancel unsolicitedCancel) {
        emsReportSender.sendUnsolicitedCancel(unsolicitedCancel.getOrderId(), unsolicitedCancel.getMessage());
    }

    private void sendEmsFill(FillEvent fillEvent) {
        emsFillSender.sendFill(fillEvent.getOrderId(), fillEvent.getExecQuantity());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // order event processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void processNewOrder(NewOrder newOrderEvent) {
        // create order object
        Order newOrder = createNewOrder(newOrderEvent);

        // sanity check
        if (!isValidNewOrderEvent(newOrderEvent, newOrder)) {
            // todo: send reject to orderer
            logger.warn("invalid order event. (orderEvent: {})", newOrderEvent);
            return;
        }

        // put order to map
        put(newOrder);

        // send order to destination
        if (newOrder.getDestination() == OrderDestination.Strategy) {
            // if strategy order, send strategy invoker
            IEvent strategyEvent = new StrategyNew(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    newOrder.getStrategy(),
                    newOrderEvent.getRequestId(),
                    new OrderView(newOrder),
                    newOrderEvent.getOrderer(),
                    newOrderEvent.getDestination()
            );
            sendStrategyEvent(newOrder.getThreadId(), strategyEvent);
        } else {
            // if exchange order, send to exchange
            sendExchangeNewOrder(newOrderEvent);
        }
    }

    @Override
    public void processAmendOrder(AmendOrder amendOrderEvent) {
        // find order object by order id and check order state
        Order currentOrder = findAndCheckOrder(amendOrderEvent.getOrderId());
        if (currentOrder == null) {
            // todo: send reject to orderer
            logger.warn("stop processing. (orderEvent: {})", amendOrderEvent);
            return;
        }

        // sanity check
        if (!isValidAmendOrderEvent(amendOrderEvent, currentOrder)) {
            // todo: send reject to orderer
            logger.warn("invalid amend order event. (amendOrderEvent: {})", amendOrderEvent);
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
        Order amendOrder = createAmendOrder(amendOrderEvent, currentOrder);
        amendOrdersByRequestId.put(amendOrderEvent.getRequestId(), amendOrder);

        // send order to destination
        if (amendOrder.getDestination() == OrderDestination.Strategy) {
            // if strategy order, send strategy invoker
            IEvent strategyEvent = new StrategyAmend(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    amendOrder.getStrategy(),
                    amendOrderEvent.getRequestId(),
                    new OrderView(currentOrder),
                    new OrderView(amendOrder),
                    currentOrder.getOrderer(),
                    currentOrder.getDestination()
            );
            sendStrategyEvent(currentOrder.getThreadId(), strategyEvent);
        } else {
            // if exchange order, send to exchange
            sendExchangeAmendOrder(amendOrderEvent);
        }
    }

    @Override
    public void processCancelOrder(CancelOrder cancelOrderEvent) {
        // find order object by order id and check order state
        Order currentOrder = findAndCheckOrder(cancelOrderEvent.getOrderId());
        if (currentOrder == null) {
            logger.warn("stop processing. (orderEvent: {})", cancelOrderEvent);
            // todo: send reject to orderer
            return;
        }

        // sanity check
        if (!isValidCancelOrderEvent(cancelOrderEvent, currentOrder)) {
            // todo: send reject to orderer
            logger.warn("invalid cancel order event. (cancelOrderEvent: {})", cancelOrderEvent);
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
            IEvent strategyEvent = new StrategyCancel(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    currentOrder.getStrategy(),
                    cancelOrderEvent.getRequestId(),
                    new OrderView(currentOrder),
                    currentOrder.getOrderer(),
                    currentOrder.getDestination()
            );
            sendStrategyEvent(currentOrder.getThreadId(), strategyEvent);
        } else {
            // if exchange order, send to exchange
            sendExchangeCancelOrder(cancelOrderEvent);
        }
    }

    private boolean isValidNewOrderEvent(NewOrder newOrderEvent, Order newOrder) {
        if (newOrderEvent.getSymbol() == null) {
            logger.warn("symbol is not set. (orderEvent: {})", newOrderEvent);
            return false;
        }

        if (newOrderEvent.getMic() == null) {
            logger.warn("mic is not set. (orderEvent: {})", newOrderEvent);
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

        if (newOrder.isStrategyOrder() && newOrder.getStrategy() == null) {
            logger.warn("strategy type invalid.");
            return false;
        }

        return true;
    }

    private boolean isValidAmendOrderEvent(AmendOrder amendOrderEvent, Order currentOrder) {
        if (amendOrderEvent.getRequestId() < 0) {
            logger.warn("request id is not set. (amendOrderEvent: {})", amendOrderEvent);
            return false;
        }

        return true;
    }

    private boolean isValidCancelOrderEvent(CancelOrder cancelOrderEvent, Order currentOrder) {
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
    public void processNewAck(NewAck newAck) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(newAck.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", newAck);
            return;
        }

        // update stored order
        order.setOrderState(OrderState.NewAck);

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", newAck);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExposedQuantity(parentStrategyOrder.getExposedQuantity().add(order.getOrderQuantity()));

            // return child order report to parent strategy
            IEvent strategyEvent = new StrategyChildOrderNewAck(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(parentStrategyOrder),
                    new OrderView(order)
            );
            sendStrategyEvent(parentStrategyOrder.getThreadId(), strategyEvent);
        } else {
            // send report event to ems
            sendEmsNewAck(newAck);
        }

        // return report event to strategy if order is a strategy order
        if (order.isStrategyOrder()) {
            IEvent strategyEvent = new StrategyNewAck(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(order)
            );
            sendStrategyEvent(order.getThreadId(), strategyEvent);
        }
    }

    @Override
    public void processNewReject(NewReject newReject) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(newReject.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", newReject);
            return;
        }

        // update stored order
        order.setRejectedQuantity(order.getOrderQuantity());
        order.setOrderState(OrderState.NewReject);

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", newReject);
                return;
            }

            // return child order report to parent strategy
            IEvent strategyEvent = new StrategyChildOrderNewReject(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(parentStrategyOrder),
                    new OrderView(order),
                    newReject.getMessage()
            );
            sendStrategyEvent(parentStrategyOrder.getThreadId(), strategyEvent);
        } else {
            // send report event to ems
            sendEmsNewReject(newReject);
        }

        // todo: do I send this before parent order?
        // return report event to strategy if the order is a strategy order
        if (order.isStrategyOrder()) {
            IEvent strategyEvent = new StrategyNewReject(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(order),
                    newReject.getMessage()
            );
            sendStrategyEvent(order.getThreadId(), strategyEvent);
        }

        // move order to terminated order list
        moveOrderToTerminatedOrderList(order);
    }

    @Override
    public void processAmendAck(AmendAck amendAck) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(amendAck.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", amendAck);
            return;
        }

        // find stored amend order by request id
        Order amendOrder = amendOrdersByRequestId.get(amendAck.getRequestId());
        if (amendOrder == null) {
            logger.warn("amend order not found. ({})", amendAck);
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
        amendOrdersByRequestId.remove(amendAck.getRequestId());

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", amendAck);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExposedQuantity(parentStrategyOrder.getExposedQuantity().add(orderQtyDiff));

            // return child order report to parent strategy
            IEvent strategyEvent = new StrategyChildOrderAmendAck(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(parentStrategyOrder),
                    new OrderView(order));
            sendStrategyEvent(parentStrategyOrder.getThreadId(), strategyEvent);
        } else {
            // send report event to ems
            sendEmsAmendAck(amendAck);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IEvent strategyEvent = new StrategyAmendAck(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),new OrderView(order)
            );
            sendStrategyEvent(order.getThreadId(), strategyEvent);
        }
    }

    @Override
    public void processAmendReject(AmendReject amendReject) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(amendReject.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", amendReject);
            return;
        }

        // find stored amend order by request id
        Order amendOrder = amendOrdersByRequestId.get(amendReject.getRequestId());
        if (amendOrder == null) {
            logger.warn("amend order not found. ({})", amendReject);
            return;
        }

        // update stored order
        order.setOrderState(OrderState.AmendReject);

        // remove stored amend order
        amendOrdersByRequestId.remove(amendReject.getRequestId());

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", amendReject);
                return;
            }

            // return child order report to parent strategy
            IEvent strategyEvent = new StrategyChildOrderAmendReject(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(parentStrategyOrder),
                    new OrderView(order),
                    amendReject.getMessage()
            );
            sendStrategyEvent(parentStrategyOrder.getThreadId(), strategyEvent);
        } else {
            // send report event to ems
            sendEmsAmendReject(amendReject);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IEvent strategyEvent = new StrategyAmendReject(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(order),
                    amendReject.getMessage()
            );
            sendStrategyEvent(order.getThreadId(), strategyEvent);
        }
    }

    @Override
    public void processCancelAck(CancelAck cancelAck) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(cancelAck.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", cancelAck);
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
                logger.warn("parent order not found. ({})", cancelAck);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExposedQuantity(parentStrategyOrder.getExposedQuantity().add(orderQtyDiff));

            // return child order report to parent strategy
            IEvent strategyEvent = new StrategyChildOrderCancelAck(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(parentStrategyOrder),
                    new OrderView(order)
            );
            sendStrategyEvent(parentStrategyOrder.getThreadId(), strategyEvent);
        } else {
            // send report event to ems
            sendEmsCancelAck(cancelAck);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IEvent strategyEvent = new StrategyCancelAck(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(order)
            );
            sendStrategyEvent(order.getThreadId(), strategyEvent);
        }

        // mover order to terminated order list
        moveOrderToTerminatedOrderList(order);
    }

    @Override
    public void processCancelReject(CancelReject cancelReject) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(cancelReject.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", cancelReject);
            return;
        }

        // update stored order
        order.setOrderState(OrderState.CancelReject);

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", cancelReject);
                return;
            }

            // return child order report to parent strategy
            IEvent strategyEvent = new StrategyChildOrderCancelReject(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(parentStrategyOrder),
                    new OrderView(order),
                    cancelReject.getMessage()
            );
            sendStrategyEvent(parentStrategyOrder.getThreadId(), strategyEvent);
        } else {
            // send report event to ems
            sendEmsCancelReject(cancelReject);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IEvent strategyEvent = new StrategyCancelReject(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(order),
                    cancelReject.getMessage()
            );
            sendStrategyEvent(order.getThreadId(), strategyEvent);
        }
    }

    @Override
    public void processUnsolicitedCancel(UnsolicitedCancel unsolicitedCancel) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(unsolicitedCancel.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (reportEvent: {})", unsolicitedCancel);
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
                logger.warn("parent order not found. ({})", unsolicitedCancel);
                return;
            }

            // update parent strategy order
            parentStrategyOrder.setExposedQuantity(parentStrategyOrder.getExposedQuantity().add(orderQtyDiff));

            // return child order report to parent strategy
            IEvent strategyEvent = new StrategyChildOrderUnsolicitedCancel(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(parentStrategyOrder),
                    new OrderView(order),
                    unsolicitedCancel.getMessage()
            );
            sendStrategyEvent(parentStrategyOrder.getThreadId(), strategyEvent);
        } else {
            // send report event to ems
            sendEmsUnsolicitedCancel(unsolicitedCancel);
        }

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            IEvent strategyEvent = new StrategyUnsolicitedCancel(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(order),
                    unsolicitedCancel.getMessage()
            );
            sendStrategyEvent(order.getThreadId(), strategyEvent);
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
            IEvent strategyEvent = new StrategyChildOrderFill(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    new OrderView(parentStrategyOrder),
                    new OrderView(order)
            );
            sendStrategyEvent(parentStrategyOrder.getThreadId(), strategyEvent);
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
            return;
        }

        if (order.isStrategyOrder()) {
            IEvent strategyEvent = new StrategyTimerEvent(
                    eventIdGenerator.generateEventId(),
                    timeSource.getCurrentTime(),
                    order.getStrategy(),
                    timerEvent.getUserTag(),
                    timerEvent.getTimerEventTime()
            );
            sendStrategyEvent(order.getThreadId(), strategyEvent);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // utilities
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private Order createNewOrder(NewOrder newOrderEvent) {
        Order parentStrategyOrder = null;
        if (newOrderEvent.getParentOrderId() != Constants.NOT_SET_ID_LONG) {
            parentStrategyOrder = get(newOrderEvent.getParentOrderId());
        }

        boolean isStrategyOrder = (newOrderEvent.getDestination() == OrderDestination.Strategy);

        Order newOrder = new Order(
                orderIdGenerator.generateId(),
                newOrderEvent.getAccountId(),
                newOrderEvent.getSymbol(),
                newOrderEvent.getMic(),
                newOrderEvent.getOrderSide(),
                newOrderEvent.getOrderQuantity(),
                newOrderEvent.getPriceLimit(),
                newOrderEvent.getOrderer(),
                newOrderEvent.getDestination(),
                newOrderEvent.getExtendedAttributes(),
                parentStrategyOrder,
                getThreadId(isStrategyOrder, parentStrategyOrder)
        );

        if (newOrder.isStrategyOrder()) {
            newOrder.setStrategy(strategyFactory.create(getStrategyTypeId(newOrderEvent::getExtendedAttribute), newOrder));
        }

        return newOrder;
    }

    private Order createAmendOrder(AmendOrder orderEvent, Order currentOrder) {
        Order amendOrder = new Order(
                currentOrder.getOrderId(),
                currentOrder.getAccountId(),
                currentOrder.getSymbol(),
                currentOrder.getMic(),
                currentOrder.getOrderSide(),
                orderEvent.getOrderQuantity(),
                orderEvent.getPriceLimit(),
                currentOrder.getOrderer(),
                currentOrder.getDestination(),
                orderEvent.getExtendedAttributes(),
                currentOrder.getParentStrategyOrder(),
                getThreadId(currentOrder.isStrategyOrder(), currentOrder.getParentStrategyOrder())
        );

        if (amendOrder.isStrategyOrder()) {
            // if strategy type amend, create new strategy
            IStrategy currentStrategy = currentOrder.getStrategy();
            int newStrategyTypeId = getStrategyTypeId(orderEvent::getExtendedAttribute);
            if (currentStrategy.getStrategyTypeId() != newStrategyTypeId) {
                amendOrder.setStrategy(strategyFactory.create(newStrategyTypeId, amendOrder));
            } else {
                amendOrder.setStrategy(currentStrategy);
            }
        }

        return amendOrder;
    }

    private int getThreadId(boolean isStrategyOrder, Order parentStrategyOrder) {
        if (!isStrategyOrder) return Constants.NOT_SET_ID_INT;

        if (parentStrategyOrder != null) return parentStrategyOrder.getThreadId();

        lastThreadId++;
        if (strategyEventExecutors.length == lastThreadId) {
            lastThreadId = 0;
        }

        return lastThreadId;
    }

    private int getStrategyTypeId(Function<String, String> extendedAttributeGetter) {
        String strId = extendedAttributeGetter.apply("strategyTypeId");
        if (strId != null) {
            return Integer.parseInt(strId);
        }

        return Integer.MIN_VALUE;
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
