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
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
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

public final class OrderManager implements IAsyncOrderListener, IAsyncReportListener, IAsyncFillListener, IAsyncTimerEventListener {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final ReportSender emsReportSender;
    private final FillSender emsFillSender;
    private final OrderSender exchangeOrderSender;
    private final EventIdGenerator eventIdGenerator;
    private final IIdGenerator orderIdGenerator = new IdGenerator(0L);
    private final ITimeSource timeSource;
    private final Executor eventExecutor;
    private final Executor[] strategyEventExecutors;
    private final Map<Long, Order> ordersByOrderId = new ConcurrentHashMap<>();
    private final Map<Long, Order> amendOrdersByRequestId = new ConcurrentHashMap<>();
    private final Map<Long, Order> terminatedOrdersByOrderId = new ConcurrentHashMap<>();

    private IStrategyFactory strategyFactory;

    private int lastThreadId = 0;

    public OrderManager(IEmsAdapter emsAdapter,
                        IExchangeAdapter exchangeAdapter,
                        EventIdGenerator eventIdGenerator,
                        ITimeSource timeSource,
                        Executor eventExecutor,
                        Executor... strategyEventExecutors) {
        this.emsReportSender = new ReportSender(eventIdGenerator, timeSource);
        this.emsFillSender = new FillSender(eventIdGenerator, timeSource);
        this.exchangeOrderSender = new OrderSender(eventIdGenerator, timeSource);
        this.eventIdGenerator = eventIdGenerator;
        this.timeSource = timeSource;
        this.eventExecutor = eventExecutor;
        this.strategyEventExecutors = strategyEventExecutors;

        emsReportSender.addAsyncEventListener(emsAdapter);
        emsFillSender.addAsyncEventListener(emsAdapter);
        exchangeOrderSender.addAsyncEventListener(exchangeAdapter);

        emsAdapter.addEventListener(this);
        exchangeAdapter.addReportListener(this);
        exchangeAdapter.addFillListener(this);
    }

    public void setStrategyFactory(IStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Executor getExecutor() {
        return eventExecutor;
    }

    // todo: revisit here
    public Map<Long, Order> getTerminatedOrdersMap() {
        return terminatedOrdersByOrderId;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // order event processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void processNewOrder(NewOrder newOrderEvent) {
        // create order object
        Order newOrder = createNewOrder(newOrderEvent);

        // sanity check
        if (!isValidNewOrderEvent(newOrderEvent, newOrder)) {
            if (newOrder.getOrderer() == Orderer.Strategy) {
                Order parentStrategyOrder = newOrder.getParentStrategyOrder();
                if (parentStrategyOrder == null) {
                    logger.warn("parent order not found. ({})", newOrderEvent);
                    return;
                }

                // send child order reject to parent strategy
                sendStrategyEvent(
                        parentStrategyOrder.getThreadId(),
                        new StrategyChildOrderNewReject(
                                eventIdGenerator.generateEventId(),
                                timeSource.getCurrentTime(),
                                newOrder.getStrategy(),
                                new OrderView(parentStrategyOrder),
                                new OrderView(newOrder),
                                newOrder.getOrderTag(),
                                "invalid order event."
                        )
                );
            } else {
                // send order reject to ems
                emsReportSender.sendNewReject(newOrderEvent.getRequestId(), newOrder.getOrderId(), "invalid order event.");
            }

            logger.warn("invalid order event. (orderEvent: {})", newOrderEvent);
            return;
        }

        // put order to map
        put(newOrder);

        // send order to destination
        if (newOrder.getDestination() == OrderDestination.Strategy) {
            // if strategy order, send strategy event
            sendStrategyEvent(
                    newOrder.getThreadId(),
                    new StrategyNew(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            newOrder.getStrategy(),
                            newOrderEvent.getRequestId(),
                            new OrderView(newOrder),
                            newOrderEvent.getOrderer(),
                            newOrderEvent.getDestination()
                    )
            );
        } else {
            // if exchange order, send to exchange
            sendExchangeNewOrder(newOrderEvent);
        }
    }

    @Override
    public void processAmendOrder(AmendOrder amendOrderEvent) {
        Order currentOrder = findAndCheckOrder(amendOrderEvent.getOrderId());
        if (currentOrder == null) {
            logger.warn("stop processing. (amendOrderEvent: {})", amendOrderEvent);
            return;
        }

        // sanity check
        if (!isValidAmendOrderEvent(amendOrderEvent, currentOrder) || !currentOrder.getOrderState().isAmendable()) {
            if (currentOrder.getOrderer() == Orderer.Strategy) {
                Order parentStrategyOrder = currentOrder.getParentStrategyOrder();
                if (parentStrategyOrder == null) {
                    logger.warn("parent order not found. (amendOrderEvent: {})", amendOrderEvent);
                    return;
                }

                // send child order reject to parent strategy
                sendStrategyEvent(
                        parentStrategyOrder.getThreadId(),
                        new StrategyChildOrderAmendReject(
                                eventIdGenerator.generateEventId(),
                                timeSource.getCurrentTime(),
                                currentOrder.getStrategy(),
                                new OrderView(parentStrategyOrder),
                                new OrderView(currentOrder),
                                currentOrder.getOrderTag(),
                                "invalid order event."
                        )
                );
            } else {
                // send order reject to ems
                emsReportSender.sendAmendReject(amendOrderEvent.getRequestId(), currentOrder.getOrderId(), "invalid order event.");
            }

            logger.warn("invalid amend order event. (amendOrderEvent: {}, orderState: {})", amendOrderEvent, currentOrder.getOrderState());
            return;
        }

        // change order state
        currentOrder.setOrderState(OrderState.Amend);

        // create amend order object which contains new parameters
        Order amendOrder = createAmendOrder(amendOrderEvent, currentOrder);
        amendOrdersByRequestId.put(amendOrderEvent.getRequestId(), amendOrder);

        // send order to destination
        if (amendOrder.getDestination() == OrderDestination.Strategy) {
            // if strategy order, send strategy event
            sendStrategyEvent(
                    currentOrder.getThreadId(),
                    new StrategyAmend(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            amendOrder.getStrategy(),
                            amendOrderEvent.getRequestId(),
                            new OrderView(currentOrder),
                            new OrderView(amendOrder),
                            currentOrder.getOrderer(),
                            currentOrder.getDestination()
                    )
            );
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
            logger.warn("stop processing. (cancelOrderEvent: {})", cancelOrderEvent);
            return;
        }

        // sanity check
        if (!isValidCancelOrderEvent(cancelOrderEvent, currentOrder) || !currentOrder.getOrderState().isCancellable()) {
            if (currentOrder.getOrderer() == Orderer.Strategy) {
                Order parentStrategyOrder = currentOrder.getParentStrategyOrder();
                if (parentStrategyOrder == null) {
                    logger.warn("parent order not found. (cancelOrderEvent: {})", cancelOrderEvent);
                    return;
                }

                // send child order reject to parent strategy
                sendStrategyEvent(
                        parentStrategyOrder.getThreadId(),
                        new StrategyChildOrderCancelReject(
                                eventIdGenerator.generateEventId(),
                                timeSource.getCurrentTime(),
                                currentOrder.getStrategy(),
                                new OrderView(parentStrategyOrder),
                                new OrderView(currentOrder),
                                currentOrder.getOrderTag(),
                                "invalid order event."
                        )
                );
            } else {
                // send order reject to ems
                emsReportSender.sendCancelReject(cancelOrderEvent.getRequestId(), currentOrder.getOrderId(), "invalid order event.");
            }

            logger.warn("invalid cancel order event. (cancelOrderEvent: {}, orderState: {})", cancelOrderEvent, currentOrder.getOrderState());
            return;
        }

        // change order state
        currentOrder.setOrderState(OrderState.Cancel);

        // send order to destination
        if (currentOrder.getDestination() == OrderDestination.Strategy) {
            // if strategy order, send strategy event
            sendStrategyEvent(
                    currentOrder.getThreadId(),
                    new StrategyCancel(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            currentOrder.getStrategy(),
                            cancelOrderEvent.getRequestId(),
                            new OrderView(currentOrder),
                            currentOrder.getOrderer(),
                            currentOrder.getDestination()
                    )
            );
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
            sendStrategyEvent(
                    parentStrategyOrder.getThreadId(),
                    new StrategyChildOrderNewAck(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(parentStrategyOrder),
                            new OrderView(order),
                            order.getOrderTag()
                    )
            );
        } else {
            // send report event to ems
            emsReportSender.sendNewAck(newAck.getRequestId(), newAck.getOrderId());
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

        // return report event to strategy if the order is a strategy order
        if (order.isStrategyOrder()) {
            sendStrategyEvent(
                    order.getThreadId(),
                    new StrategyNewReject(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(order),
                            newReject.getMessage()
                    )
            );
        }

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", newReject);
                return;
            }

            // return child order report to parent strategy
            sendStrategyEvent(
                    parentStrategyOrder.getThreadId(),
                    new StrategyChildOrderNewReject(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(parentStrategyOrder),
                            new OrderView(order),
                            order.getOrderTag(),
                            newReject.getMessage()
                    )
            );
        } else {
            // send report event to ems
            emsReportSender.sendNewReject(newReject.getRequestId(), newReject.getOrderId(), newReject.getMessage());

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

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            sendStrategyEvent(
                    order.getThreadId(),
                    new StrategyAmendAck(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),new OrderView(order)
                    )
            );
        }

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
            sendStrategyEvent(
                    parentStrategyOrder.getThreadId(),
                    new StrategyChildOrderAmendAck(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(parentStrategyOrder),
                            new OrderView(order),
                            order.getOrderTag()
                    )
            );
        } else {
            // send report event to ems
            emsReportSender.sendAmendAck(amendAck.getRequestId(), amendAck.getOrderId());
        }
    }

    @Override
    public void processAmendReject(AmendReject amendReject) {
        // find order object by order id and check order state
        Order order = findAndCheckOrder(amendReject.getOrderId());
        if (order == null) {
            logger.warn("stop processing. (amendReject: {})", amendReject);
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

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            sendStrategyEvent(
                    order.getThreadId(),
                    new StrategyAmendReject(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(order),
                            amendReject.getMessage()
                    )
            );
        }

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", amendReject);
                return;
            }

            // return child order report to parent strategy
            sendStrategyEvent(
                    parentStrategyOrder.getThreadId(),
                    new StrategyChildOrderAmendReject(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(parentStrategyOrder),
                            new OrderView(order),
                            order.getOrderTag(),
                            amendReject.getMessage()
                    )
            );
        } else {
            // send report event to ems
            emsReportSender.sendAmendReject(amendReject.getRequestId(), amendReject.getOrderId(), amendReject.getMessage());
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

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            sendStrategyEvent(
                    order.getThreadId(),
                    new StrategyCancelAck(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(order)
                    )
            );
        }

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
            sendStrategyEvent(
                    parentStrategyOrder.getThreadId(),
                    new StrategyChildOrderCancelAck(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(parentStrategyOrder),
                            new OrderView(order),
                            order.getOrderTag()
                    )
            );
        } else {
            // send report event to ems
            emsReportSender.sendCancelAck(cancelAck.getRequestId(), cancelAck.getOrderId());
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

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            sendStrategyEvent(
                    order.getThreadId(),
                    new StrategyCancelReject(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(order),
                            cancelReject.getMessage()
                    )
            );
        }

        // send report to orderer
        if (order.getOrderer() == Orderer.Strategy) {
            Order parentStrategyOrder = order.getParentStrategyOrder();
            if (parentStrategyOrder == null) {
                logger.warn("parent order not found. ({})", cancelReject);
                return;
            }

            // return child order report to parent strategy
            sendStrategyEvent(
                    parentStrategyOrder.getThreadId(),
                    new StrategyChildOrderCancelReject(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(parentStrategyOrder),
                            new OrderView(order),
                            order.getOrderTag(),
                            cancelReject.getMessage()
                    )
            );
        } else {
            // send report event to ems
            emsReportSender.sendCancelReject(cancelReject.getRequestId(), cancelReject.getOrderId(), cancelReject.getMessage());
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

        // return report event to strategy if the order is strategy order
        if (order.isStrategyOrder()) {
            sendStrategyEvent(
                    order.getThreadId(),
                    new StrategyUnsolicitedCancel(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(order),
                            unsolicitedCancel.getMessage()
                    )
            );
        }

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
            sendStrategyEvent(
                    parentStrategyOrder.getThreadId(),
                    new StrategyChildOrderUnsolicitedCancel(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            new OrderView(parentStrategyOrder),
                            new OrderView(order),
                            order.getOrderTag(),
                            unsolicitedCancel.getMessage()
                    )
            );
        } else {
            // send report event to ems
            emsReportSender.sendUnsolicitedCancel(unsolicitedCancel.getOrderId(), unsolicitedCancel.getMessage());
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
            sendStrategyEvent(
                    parentStrategyOrder.getThreadId(),
                    new StrategyChildOrderFill(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            fillEvent.getExecQuantity(),
                            new OrderView(parentStrategyOrder),
                            new OrderView(order),
                            order.getOrderTag()
                    )
            );
        } else {
            // send fill event to ems
            emsFillSender.sendFill(fillEvent.getOrderId(), fillEvent.getExecQuantity());
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
             sendStrategyEvent(
                    order.getThreadId(),
                    new StrategyTimerEvent(
                            eventIdGenerator.generateEventId(),
                            timeSource.getCurrentTime(),
                            order.getStrategy(),
                            timerEvent.getTimerTag(),
                            timerEvent.getTimerEventTime()
                    )
            );
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
                newOrderEvent.getExtendedAttribute("orderTag"),
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
            newOrder.setStrategy(createStrategy(newOrder.getExtendedAttribute("strategyName"), newOrder));
        }

        return newOrder;
    }

    private Order createAmendOrder(AmendOrder orderEvent, Order currentOrder) {
        Order amendOrder = new Order(
                currentOrder.getOrderId(),
                currentOrder.getAccountId(),
                currentOrder.getSymbol(),
                currentOrder.getMic(),
                currentOrder.getOrderTag(),
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
            String newStrategyName = orderEvent.getExtendedAttribute("strategyName");
            if (!currentStrategy.getStrategyDescriptor().getName().equals(newStrategyName)) {
                amendOrder.setStrategy(createStrategy(newStrategyName, amendOrder));
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

//    private String getOrderTag(Function<String, String> extendedAttributeGetter) {
//        return extendedAttributeGetter.apply("orderTag");
//    }
//
//    private String getStrategyName(Function<String, String> extendedAttributeGetter) {
//        return extendedAttributeGetter.apply("strategyName");
//    }

    private IStrategy createStrategy(String strategyName, IOrder order) {
        if (strategyFactory == null) {
            return null;
        }

        return strategyFactory.create(strategyName, order);
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
