package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.bunkyo.config.IConfigAccessor;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.master.market.IMarket;
import jp.co.myogadanimotors.bunkyo.master.product.IProduct;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.kohinata.common.Constants;
import jp.co.myogadanimotors.kohinata.event.RequestIdGenerator;
import jp.co.myogadanimotors.kohinata.event.marketdata.IAsyncMarketDataRequestListener;
import jp.co.myogadanimotors.kohinata.event.marketdata.MarketDataRequestSender;
import jp.co.myogadanimotors.kohinata.event.order.IAsyncOrderListener;
import jp.co.myogadanimotors.kohinata.event.report.FillSender;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncFillListener;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncReportListener;
import jp.co.myogadanimotors.kohinata.event.report.ReportSender;
import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerRegistrationListener;
import jp.co.myogadanimotors.kohinata.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.OrderState;
import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.StrategyState;
import jp.co.myogadanimotors.kohinata.strategy.event.BaseStrategyEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.childorder.*;
import jp.co.myogadanimotors.kohinata.strategy.event.childorderfill.StrategyChildOrderFill;
import jp.co.myogadanimotors.kohinata.strategy.event.marketdata.StrategyMarketDataEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.order.*;
import jp.co.myogadanimotors.kohinata.strategy.event.timer.StrategyTimerEvent;
import jp.co.myogadanimotors.kohinata.strategy.validator.IValidator;
import jp.co.myogadanimotors.kohinata.strategy.validator.RejectMessage;
import jp.co.myogadanimotors.kohinata.strategy.validator.StrategyStateValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static jp.co.myogadanimotors.kohinata.strategy.StrategyState.Working;

public final class StrategyContext implements IStrategyContext, IStrategyEventListener {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final RequestIdGenerator requestIdGenerator;
    private final ChildOrderContainer childOrderContainer;
    private final IChildOrderSender childOrderSender;
    private final TimerRegistry timerRegistry;
    private final ReportSender reportSender;
    private final FillSender fillSender;
    private final MarketDataRequestSender marketDataRequestSender;
    private final ITimeSource timeSource;
    private final OrderView orderView;
    private final MarketView marketView;
    private final IProduct product;
    private final IStrategyDescriptor strategyDescriptor;
    private final IStrategy strategy;
    private final StrategyParameterAccessor strategyParameterAccessor;
    private final List<IValidator> validatorList = new ArrayList<>();

    private BaseStrategyEvent lastStrategyEvent;
    private StrategyState strategyState;
    private IStrategyPendingAmendContext pendingAmendContext;
    private IStrategyPendingAmendProcessor pendingAmendProcessor;
    private IStrategyPendingCancelProcessor pendingCancelProcessor;
    private long currentTime;

    public StrategyContext(EventIdGenerator eventIdGenerator,
                           RequestIdGenerator requestIdGenerator,
                           ITimeSource timeSource,
                           IOrder order,
                           IMarket market,
                           IProduct product,
                           IStrategyDescriptor strategyDescriptor,
                           IStrategy strategy,
                           IConfigAccessor strategyConfigAccessor,
                           IAsyncOrderListener asyncOrderListener,
                           IAsyncReportListener asyncReportListener,
                           IAsyncFillListener asyncFillListener,
                           IAsyncMarketDataRequestListener asyncMarketDataRequestListener,
                           IAsyncTimerRegistrationListener asyncTimerRegistrationListener) {
        this.requestIdGenerator = requireNonNull(requestIdGenerator);
        this.childOrderContainer = new ChildOrderContainer();
        this.childOrderSender = new ChildOrderSender(eventIdGenerator, requestIdGenerator, timeSource, childOrderContainer, order, asyncOrderListener);
        this.timerRegistry = new TimerRegistry(order.getOrderId(), eventIdGenerator, timeSource, asyncTimerRegistrationListener);
        this.reportSender = new ReportSender(eventIdGenerator, timeSource);
        this.fillSender = new FillSender(eventIdGenerator, timeSource);
        this.marketDataRequestSender = new MarketDataRequestSender(eventIdGenerator, timeSource);
        this.timeSource = requireNonNull(timeSource);
        this.orderView = new OrderView(order);
        this.marketView = new MarketView(market);
        this.product = requireNonNull(product);
        this.strategyDescriptor = requireNonNull(strategyDescriptor);
        this.strategy = requireNonNull(strategy);
        this.strategyParameterAccessor = new StrategyParameterAccessor(strategyDescriptor.getName(), strategyConfigAccessor);

        validatorList.addAll(strategy.getValidatorList());
        validatorList.add(new StrategyStateValidator());
        strategyParameterAccessor.updateExtendedAttributes(order.getExtendedAttributes());
        strategyState = StrategyState.PendingNew;
        currentTime = timeSource.getCurrentTime();

        reportSender.addAsyncEventListener(asyncReportListener);
        fillSender.addAsyncEventListener(asyncFillListener);
        marketDataRequestSender.addAsyncEventListener(asyncMarketDataRequestListener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // IStrategyContext override methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public IOrder getOrder() {
        return orderView;
    }

    @Override
    public IMarket getMarket() {
        return marketView;
    }

    @Override
    public IProduct getProduct() {
        return product;
    }

    @Override
    public IStrategyDescriptor getStrategyDescriptor() {
        return strategyDescriptor;
    }

    @Override
    public IStrategyParameterAccessor getStrategyParameterAccessor() {
        return strategyParameterAccessor;
    }

    @Override
    public IChildOrderContainer getChildOrderContainer() {
        return childOrderContainer;
    }

    @Override
    public IChildOrderSender getChildOrderSender() {
        return childOrderSender;
    }

    @Override
    public ITimerRegistry getTimerRegistry() {
        return timerRegistry;
    }

    @Override
    public BaseStrategyEvent getLastStrategyEvent() {
        return lastStrategyEvent;
    }

    @Override
    public StrategyState getStrategyState() {
        return strategyState;
    }

    @Override
    public long getCurrentTime() {
        return currentTime;
    }

    @Override
    public void subscribeMarketData(String symbol, String mic) {
        marketDataRequestSender.sendMarketDataRequest(
                requestIdGenerator.generateRequestId(),
                product.getId(),
                symbol,
                mic
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // strategy event processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Executor getEventQueue() {
        // todo: to be implemented
        return null;
    }

    @Override
    public final void preProcessEvent(BaseStrategyEvent strategyEvent) {
        this.lastStrategyEvent = strategyEvent;
        currentTime = timeSource.getCurrentTime();
    }

    @Override
    public final void postProcessEvent() {
        processStrategyState();
    }

    //////////////////////////////////////////////////
    // order event
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyNew(StrategyNew strategyNew) {
        // update context
        orderView.update(strategyNew.getOrderView());

        // initialize strategy
        strategy.init(this);

        // validate new order
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validatorList, (validator) -> validator.isValidStrategyRequestNew(strategyNew, this, rejectMessages))) {
            reportSender.sendNewAck(getOrder().getOrderId(), strategyNew.getRequestId());
        } else {
            strategyState = StrategyState.Rejected;
            reportSender.sendNewReject(getOrder().getOrderId(), strategyNew.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    @Override
    public final void processStrategyAmend(StrategyAmend strategyAmend) {
        // update context
        orderView.update(strategyAmend.getOrderView());

        // todo: call init() here if type amend

        // create pending amend context
        pendingAmendContext = strategy.createPendingAmendContext(this);

        // validate amend order
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validatorList, (validator) -> validator.isValidStrategyRequestAmend(strategyAmend, this, pendingAmendContext, rejectMessages))) {
            strategyState = StrategyState.PendingAmend;
            pendingAmendProcessor = strategy.createPendingAmendProcessor(this, strategyAmend.getRequestId());
            getTimerRegistry().registerRepetitiveTimer(
                    Constants.PENDING_AMEND_CANCEL_REPETITIVE_TIMER_TAG,
                    strategyParameterAccessor.getLong("pendingAmendProcessingTimerInterval", Constants.DEFAULT_PENDING_AMEND_PROCESSING_TIMER_INTERVAL),
                    getCurrentTime()
            );
        } else {
            reportSender.sendAmendReject(getOrder().getOrderId(), strategyAmend.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    @Override
    public final void processStrategyCancel(StrategyCancel strategyCancel) {
        // update context
        orderView.update(strategyCancel.getOrderView());

        // validate cancel order
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validatorList, (validator) -> validator.isValidStrategyRequestCancel(strategyCancel, this, rejectMessages))) {
            strategyState = StrategyState.PendingCancel;
            pendingCancelProcessor = strategy.createPendingCancelProcessor(this, strategyCancel.getRequestId());
            getTimerRegistry().registerRepetitiveTimer(
                    Constants.PENDING_AMEND_CANCEL_REPETITIVE_TIMER_TAG,
                    strategyParameterAccessor.getLong("pendingCancelProcessingTimerInterval", Constants.DEFAULT_PENDING_CANCEL_PROCESSING_TIMER_INTERVAL),
                    getCurrentTime()
            );
        } else {
            reportSender.sendCancelReject(getOrder().getOrderId(), strategyCancel.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    //////////////////////////////////////////////////
    // order report event
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyNewAck(StrategyNewAck strategyNewAck) {
        // update context
        orderView.update(strategyNewAck.getOrderView());
        strategyState = Working;
    }

    @Override
    public final void processStrategyNewReject(StrategyNewReject strategyNewReject) {
        // update context
        orderView.update(strategyNewReject.getOrderView());
        strategyState = StrategyState.Rejected;
    }

    @Override
    public final void processStrategyAmendAck(StrategyAmendAck strategyAmendAck) {
        // update context
        orderView.update(strategyAmendAck.getOrderView());
        strategyState = Working;

        Map<String, String> extendedAttributes = strategyAmendAck.getOrderView().getExtendedAttributes();
        orderView.updateExtendedAttributes(extendedAttributes);
        strategyParameterAccessor.updateExtendedAttributes(extendedAttributes);
    }

    @Override
    public final void processStrategyAmendReject(StrategyAmendReject strategyAmendReject) {
        // update context
        orderView.update(strategyAmendReject.getOrderView());
        strategyState = Working;
    }

    @Override
    public final void processStrategyCancelAck(StrategyCancelAck strategyCancelAck) {
        // update context
        orderView.update(strategyCancelAck.getOrderView());
        strategyState = StrategyState.Cancelled;
    }

    @Override
    public final void processStrategyCancelReject(StrategyCancelReject strategyCancelReject) {
        // update context
        orderView.update(strategyCancelReject.getOrderView());
        strategyState = Working;
    }

    @Override
    public final void processStrategyUnsolicitedCancel(StrategyUnsolicitedCancel strategyUnsolicitedCancel) {
        // update context
        orderView.update(strategyUnsolicitedCancel.getOrderView());
        strategyState = StrategyState.UnsolicitedCancel;
    }

    //////////////////////////////////////////////////
    // child order report
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyChildOrderNewAck(StrategyChildOrderNewAck strategyChildOrderNewAck) {
        // update context
        orderView.update(strategyChildOrderNewAck.getOrderView());
        childOrderContainer.addChildOrder(strategyChildOrderNewAck.getChildOrderView());
        childOrderContainer.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderNewReject(StrategyChildOrderNewReject strategyChildOrderNewReject) {
        // update context
        orderView.update(strategyChildOrderNewReject.getOrderView());
        childOrderContainer.updateChildOrder(strategyChildOrderNewReject.getChildOrderView());
        childOrderContainer.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderAmendAck(StrategyChildOrderAmendAck strategyChildOrderAmendAck) {
        // update context
        orderView.update(strategyChildOrderAmendAck.getOrderView());
        childOrderContainer.updateChildOrder(strategyChildOrderAmendAck.getChildOrderView());
        childOrderContainer.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderAmendReject(StrategyChildOrderAmendReject strategyChildOrderAmendReject) {
        // update context
        orderView.update(strategyChildOrderAmendReject.getOrderView());
        childOrderContainer.updateChildOrder(strategyChildOrderAmendReject.getChildOrderView());
        childOrderContainer.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderCancelAck(StrategyChildOrderCancelAck strategyChildOrderCancelAck) {
        // update context
        orderView.update(strategyChildOrderCancelAck.getOrderView());
        childOrderContainer.removeChildOrder(strategyChildOrderCancelAck.getChildOrderView());
        childOrderContainer.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderCancelReject(StrategyChildOrderCancelReject strategyChildOrderCancelReject) {
        // update context
        orderView.update(strategyChildOrderCancelReject.getOrderView());
        childOrderContainer.updateChildOrder(strategyChildOrderCancelReject.getChildOrderView());
        childOrderContainer.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderExpire(StrategyChildOrderExpire strategyChildOrderExpire) {
        // update context
        orderView.update(strategyChildOrderExpire.getOrderView());
        childOrderContainer.removeChildOrder(strategyChildOrderExpire.getChildOrderView());
    }

    @Override
    public final void processStrategyChildOrderUnsolicitedCancel(StrategyChildOrderUnsolicitedCancel strategyChildOrderUnsolicitedCancel) {
        // update context
        orderView.update(strategyChildOrderUnsolicitedCancel.getOrderView());
        childOrderContainer.removeChildOrder(strategyChildOrderUnsolicitedCancel.getChildOrderView());
    }

    //////////////////////////////////////////////////
    // child order fill
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyChildOrderFill(StrategyChildOrderFill strategyChildOrderFill) {
        // update context
        orderView.update(strategyChildOrderFill.getOrderView());
        if (getOrder().getOrderState() == OrderState.FullyFilled) strategyState = StrategyState.FullyFilled;
        if (strategyChildOrderFill.getChildOrderView().getOrderState() == OrderState.FullyFilled) {
            childOrderContainer.removeChildOrder(strategyChildOrderFill.getChildOrderView());
        } else {
            childOrderContainer.updateChildOrder(strategyChildOrderFill.getChildOrderView());
        }

        // send fill event to parent order
        fillSender.sendFill(getOrder().getOrderId(), strategyChildOrderFill.getExecQuantity());
    }

    //////////////////////////////////////////////////
    // market data event
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyMarketDataEvent(StrategyMarketDataEvent strategyMarketDataEvent) {
        logger.trace("processing market data.");
    }

    //////////////////////////////////////////////////
    // timer event event
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyTimerEvent(StrategyTimerEvent strategyTimerEvent) {
        logger.trace("processing timer event.");

        // update context
        timerRegistry.onTimer(strategyTimerEvent.getTimerTag(), strategyTimerEvent.getTimerEventTime());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // strategy state processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void processStrategyState() {
        switch (strategyState) {
            case Working:           processStrategyStateWorking(); break;
            case Rejected:          processStrategyStateRejected(); break;
            case FullyFilled:       processStrategyStateFullyFilled(); break;
            case Cancelled:         processStrategyStateCancelled(); break;
            case UnsolicitedCancel: processStrategyStateUnsolicitedCancel(); break;
            case PendingNew:        processStrategyStatePendingNew(); break;
            case PendingAmend:      processStrategyStatePendingAmend(); break;
            case PendingCancel:     processStrategyStatePendingCancel(); break;
            case PostAmend:         processStrategyStatePostAmend(); break;
            case PostCancel:        processStrategyStatePostCancel(); break;
        }
    }

    private void processStrategyStateWorking() {
        logger.trace("processing working state.");
        if (canProcessWorkingState()) {
            strategy.doAction(this);
        }
    }

    private void processStrategyStateRejected() {
        logger.info("new order is rejected.");
        strategy.terminate(this);
    }

    private void processStrategyStateFullyFilled() {
        logger.info("new order is fully filled.");
        strategy.terminate(this);
    }

    private void processStrategyStateCancelled() {
        logger.info("new order is cancelled.");
        strategy.terminate(this);
    }

    private void processStrategyStateUnsolicitedCancel() {
        logger.info("new order is unsolicited cancelled.");
        strategy.terminate(this);
    }

    private void processStrategyStatePendingNew() {
        logger.trace("not doing anything.");
    }

    private void processStrategyStatePendingAmend() {
        logger.trace("processing pending amend state.");

        pendingAmendProcessor.process(pendingAmendContext);

        if (pendingAmendProcessor.getResult() == PendingAmendCancelResult.Working) {
            return;
        }

        strategyState = StrategyState.PostAmend;
        getTimerRegistry().removeRepetitiveTimer(Constants.PENDING_AMEND_CANCEL_REPETITIVE_TIMER_TAG);

        if (pendingAmendProcessor.getResult() == PendingAmendCancelResult.Succeeded) {
            logger.info("PendingAmendProcessor succeeded.");
            reportSender.sendAmendAck(getOrder().getOrderId(), pendingAmendProcessor.getRequestId());
        } else if (pendingAmendProcessor.getResult() == PendingAmendCancelResult.Failed) {
            logger.info("PendingAmendProcessor failed.");
            reportSender.sendAmendReject(getOrder().getOrderId(), pendingAmendProcessor.getRequestId(), pendingAmendProcessor.getMessage());
        }
    }

    private void processStrategyStatePendingCancel() {
        logger.trace("processing pending cancel state.");

        pendingCancelProcessor.process();

        if (pendingCancelProcessor.getResult() == PendingAmendCancelResult.Working) {
            return;
        }

        strategyState = StrategyState.PostCancel;
        getTimerRegistry().removeRepetitiveTimer(Constants.PENDING_AMEND_CANCEL_REPETITIVE_TIMER_TAG);

        if (pendingCancelProcessor.getResult() == PendingAmendCancelResult.Succeeded) {
            logger.info("PendingCancelProcessor succeeded.");
            reportSender.sendCancelAck(getOrder().getOrderId(), pendingCancelProcessor.getRequestId());
        } else if (pendingCancelProcessor.getResult() == PendingAmendCancelResult.Failed) {
            logger.info("PendingCancelProcessor failed.");
            reportSender.sendCancelReject(getOrder().getOrderId(), pendingCancelProcessor.getRequestId(), pendingCancelProcessor.getMessage());
        }
    }

    private void processStrategyStatePostAmend() {
        logger.trace("not doing anything");
    }

    private void processStrategyStatePostCancel() {
        logger.trace("not doing anything");
    }

    private void checkSpamming() {
        logger.trace("checking spamming.");
        // todo: if spamming happened, change strategy state to "UnsolicitedCancel"
    }

    private boolean canProcessWorkingState() {
        if (childOrderContainer.hasOnTheWireChildOrders()) {
            logger.trace("cannot proceed since some on-the-wire child orders exist.");
            return false;
        }

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // utilities
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean isValid(List<IValidator> validatorList, Predicate<IValidator> predicate) {
        boolean isValid = true;
        for (IValidator validator : validatorList) {
            isValid = isValid && predicate.test(validator);
        }
        return isValid;
    }

    private static String combineRejectMessages(List<RejectMessage> rejectMessages) {
        StringBuilder sb = new StringBuilder();
        for (RejectMessage message : rejectMessages) {
            if (sb.length() > 0) sb.append(";");
            sb.append(message.get());
        }
        return sb.toString();
    }
}
