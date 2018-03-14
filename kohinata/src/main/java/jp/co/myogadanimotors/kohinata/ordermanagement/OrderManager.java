package jp.co.myogadanimotors.kohinata.ordermanagement;

import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.bunkyo.idgenerator.IdGenerator;
import jp.co.myogadanimotors.bunkyo.master.market.MarketMaster;
import jp.co.myogadanimotors.bunkyo.master.product.ProductMaster;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.kohinata.common.Constants;
import jp.co.myogadanimotors.kohinata.event.order.*;
import jp.co.myogadanimotors.kohinata.event.report.*;
import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerEventListener;
import jp.co.myogadanimotors.kohinata.event.timer.TimerEvent;
import jp.co.myogadanimotors.kohinata.master.extendedattriute.ExtendedAttributeMaster;
import jp.co.myogadanimotors.kohinata.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.kohinata.master.strategy.StrategyMaster;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.Order;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.OrderState;
import jp.co.myogadanimotors.kohinata.strategy.IStrategyFactory;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContextFactory;
import jp.co.myogadanimotors.kohinata.strategy.event.childorder.*;
import jp.co.myogadanimotors.kohinata.strategy.event.childorderfill.StrategyChildOrderFill;
import jp.co.myogadanimotors.kohinata.strategy.event.order.*;
import jp.co.myogadanimotors.kohinata.strategy.event.timer.StrategyTimerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static java.util.Objects.requireNonNull;

public final class OrderManager implements IAsyncOrderListener, IAsyncReportListener, IAsyncFillListener, IAsyncTimerEventListener {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final ReportSender emsReportSender;
    private final FillSender emsFillSender;
    private final OrderSender exchangeOrderSender;
    private final EventIdGenerator eventIdGenerator;
    private final IdGenerator orderIdGenerator = new IdGenerator(0L);
    private final ITimeSource timeSource;
    private final OrderValidator orderValidator;
    private final StrategyContextFactory strategyContextFactory;
    private final StrategyMaster strategyMaster;
    private final Executor eventQueue;
    private final Executor[] strategyEventQueues;
    private final Map<Long, Order> ordersByOrderId = new ConcurrentHashMap<>();
    private final Map<Long, Order> amendOrdersByRequestId = new ConcurrentHashMap<>();
    private final Map<Long, Order> terminatedOrdersByOrderId = new ConcurrentHashMap<>();

    private int lastThreadId = 0;

    public OrderManager(EventIdGenerator eventIdGenerator,
                        ITimeSource timeSource,
                        StrategyContextFactory strategyContextFactory,
                        MarketMaster marketMaster,
                        ProductMaster productMaster,
                        ExtendedAttributeMaster extendedAttributeMaster,
                        StrategyMaster strategyMaster,
                        Executor eventQueue,
                        Executor... strategyEventQueues) {
        this.emsReportSender = new ReportSender(requireNonNull(eventIdGenerator), requireNonNull(timeSource));
        this.emsFillSender = new FillSender(eventIdGenerator, timeSource);
        this.exchangeOrderSender = new OrderSender(eventIdGenerator, timeSource);
        this.eventIdGenerator = requireNonNull(eventIdGenerator);
        this.timeSource = requireNonNull(timeSource);
        this.orderValidator =  new OrderValidator(marketMaster, productMaster, extendedAttributeMaster);
        this.strategyContextFactory = requireNonNull(strategyContextFactory);
        this.strategyMaster = requireNonNull(strategyMaster);
        this.eventQueue = requireNonNull(eventQueue);
        this.strategyEventQueues = requireNonNull(strategyEventQueues);
    }

    public void addEventListeners(IAsyncReportListener emsReportListener, IAsyncFillListener emsFillListener, IAsyncOrderListener exchangeOrderListener) {
        emsReportSender.addAsyncEventListener(emsReportListener);
        emsFillSender.addAsyncEventListener(emsFillListener);
        exchangeOrderSender.addAsyncEventListener(exchangeOrderListener);
    }

    public void addStrategyFactories(IStrategyFactory strategyFactory) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Executor getEventQueue() {
        return eventQueue;
    }

    // todo: revisit here
    public Map<Long, Order> getTerminatedOrdersMap() {
        return terminatedOrdersByOrderId;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // transaction senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendStrategyEvent(int threadId, IEvent strategyEvent) {
        Executor strategyEventExecutor = strategyEventQueues[threadId];
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

        // validation
        if (!orderValidator.isValidNewOrder(newOrderEvent, newOrder)) {
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
                                newOrder.getStrategyContext(),
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
                            newOrder.getStrategyContext(),
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

        // validation
        if (!orderValidator.isValidAmendOrder(amendOrderEvent, currentOrder)) {
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
                                currentOrder.getStrategyContext(),
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
                            amendOrder.getStrategyContext(),
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

        // validation
        if (!orderValidator.isValidCancelOrder(cancelOrderEvent, currentOrder)) {
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
                                currentOrder.getStrategyContext(),
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
                            currentOrder.getStrategyContext(),
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
                    order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
                            new OrderView(order)
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
                            order.getStrategyContext(),
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
            newOrder.setStrategyContext(createStrategyContext(getStrategyName(newOrder), newOrder));
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
            StrategyContext currentStrategyContext = currentOrder.getStrategyContext();
            String newStrategyName = getStrategyName(amendOrder);
            if (!currentStrategyContext.getStrategyDescriptor().getName().equals(newStrategyName)) {
                amendOrder.setStrategyContext(createStrategyContext(newStrategyName, amendOrder));
            } else {
                amendOrder.setStrategyContext(currentStrategyContext);
            }
        }

        return amendOrder;
    }

    private int getThreadId(boolean isStrategyOrder, Order parentStrategyOrder) {
        if (!isStrategyOrder) return Constants.NOT_SET_ID_INT;

        if (parentStrategyOrder != null) return parentStrategyOrder.getThreadId();

        lastThreadId++;
        if (strategyEventQueues.length == lastThreadId) {
            lastThreadId = 0;
        }

        return lastThreadId;
    }

    private String getStrategyName(IOrder order) {
        return order.getExtendedAttribute("strategyName");
    }

    private StrategyContext createStrategyContext(String strategyName, IOrder order) {
        IStrategyDescriptor strategyDescriptor = strategyMaster.getByName(strategyName);
        if (strategyDescriptor == null) {
            logger.warn("strategyName invalid. (strategyName: {})", strategyName);
            return null;
        }

        return strategyContextFactory.create(order, strategyDescriptor);
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
