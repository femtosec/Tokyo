package jp.co.myogadanimotors.kohinata.strategy;

import jp.co.myogadanimotors.kohinata.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.OrderState;
import jp.co.myogadanimotors.kohinata.strategy.context.*;
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
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static jp.co.myogadanimotors.kohinata.strategy.StrategyState.Working;

public abstract class AbstractStrategy implements IStrategy {

    protected final Logger logger = LogManager.getLogger(getClass().getName());

    private final IStrategyDescriptor strategyDescriptor;
    private final StrategyContext context;
    private final IStrategyParameters strategyParameters;
    private final List<IValidator> validators = new ArrayList<>();

    public AbstractStrategy(IStrategyDescriptor strategyDescriptor, StrategyContext context, IStrategyParameters strategyParameters) {
        this.strategyDescriptor = requireNonNull(strategyDescriptor);
        this.context = requireNonNull(context);
        this.strategyParameters = requireNonNull(strategyParameters);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // do action
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doAction();

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public final IStrategyDescriptor getStrategyDescriptor() {
        return strategyDescriptor;
    }

    protected final IStrategyContext getStrategyContext() {
        return context;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // validation related
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected final void addValidator(IValidator validator) {
        validators.add(validator);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // strategy lifecycle related
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void init() {
        // init strategy parameters
        strategyParameters.init();

        // add validators
        addValidator(new StrategyStateValidator());
    }

    protected void terminate(String message) {
        logger.info("terminating strategy. ({})", message);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // pending amend/cancel related
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected IStrategyPendingAmendContext createPendingAmendContext() {
        return new StrategyPendingAmendContext();
    }

    protected IStrategyPendingAmendProcessor createPendingAmendProcessor(long requestId) {
        return new StrategyPendingAmendProcessor(
                requestId,
                context.getChildOrderContainer(),
                context.getChildOrderSender(),
                strategyParameters.getMaxNumberOfPendingAmendProcessing()
        );
    }

    protected IStrategyPendingCancelProcessor createPendingCancelProcessor(long requestId) {
        return new StrategyPendingCancelProcessor(
                requestId,
                context.getChildOrderContainer(),
                context.getChildOrderSender(),
                strategyParameters.getMaxNumberOfPendingCancelProcessing()
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // strategy event processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public final void preProcessEvent() {
        context.refreshCurrentTime();
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
        context.updateOrderView(strategyNew.getOrderView());

        // initialize strategy
        init();

        // validate new order
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validators, (validator) -> validator.isValidStrategyRequestNew(strategyNew, context, rejectMessages))) {
            context.getReportSender().sendNewAck(context.getOrder().getOrderId(), strategyNew.getRequestId());
        } else {
            context.setStrategyState(StrategyState.Rejected);
            context.getReportSender().sendNewReject(context.getOrder().getOrderId(), strategyNew.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    @Override
    public final void processStrategyAmend(StrategyAmend strategyAmend) {
        // update context
        context.updateOrderView(strategyAmend.getOrderView());

        // todo: call init() here if type amend

        // create pending amend context
        IStrategyPendingAmendContext pac = createPendingAmendContext();

        // validate amend order
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validators, (validator) -> validator.isValidStrategyRequestAmend(strategyAmend, context, pac, rejectMessages))) {
            context.setStrategyState(StrategyState.PendingAmend);
            context.setStrategyPendingAmendProcessor(createPendingAmendProcessor(strategyAmend.getRequestId()));
            context.getTimerRegistry().registerRepetitiveTimer(
                    strategyParameters.getPendingAmendCancelRepetitiveTimerTag(),
                    strategyParameters.getPendingAmendProcessingTimerInterval(),
                    context.getCurrentTime()
            );
        } else {
            context.getReportSender().sendAmendReject(context.getOrder().getOrderId(), strategyAmend.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    @Override
    public final void processStrategyCancel(StrategyCancel strategyCancel) {
        // update context
        context.updateOrderView(strategyCancel.getOrderView());

        // validate cancel order
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validators, (validator) -> validator.isValidStrategyRequestCancel(strategyCancel, context, rejectMessages))) {
            context.setStrategyState(StrategyState.PendingCancel);
            context.setStrategyPendingCancelProcessor(createPendingCancelProcessor(strategyCancel.getRequestId()));
            context.getTimerRegistry().registerRepetitiveTimer(
                    strategyParameters.getPendingAmendCancelRepetitiveTimerTag(),
                    strategyParameters.getPendingCancelProcessingTimerInterval(),
                    context.getCurrentTime()
            );
        } else {
            context.getReportSender().sendCancelReject(context.getOrder().getOrderId(), strategyCancel.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    //////////////////////////////////////////////////
    // order report event
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyNewAck(StrategyNewAck strategyNewAck) {
        // update context
        context.updateOrderView(strategyNewAck.getOrderView());
        context.setStrategyState(Working);
    }

    @Override
    public final void processStrategyNewReject(StrategyNewReject strategyNewReject) {
        // update context
        context.updateOrderView(strategyNewReject.getOrderView());
        context.setStrategyState(StrategyState.Rejected);
    }

    @Override
    public final void processStrategyAmendAck(StrategyAmendAck strategyAmendAck) {
        // update context
        context.updateOrderView(strategyAmendAck.getOrderView());
        context.updateExtendedAttributes(strategyAmendAck.getOrderView().getExtendedAttributes());
        context.setStrategyState(Working);
    }

    @Override
    public final void processStrategyAmendReject(StrategyAmendReject strategyAmendReject) {
        // update context
        context.updateOrderView(strategyAmendReject.getOrderView());
        context.setStrategyState(Working);
    }

    @Override
    public final void processStrategyCancelAck(StrategyCancelAck strategyCancelAck) {
        // update context
        context.updateOrderView(strategyCancelAck.getOrderView());
        context.setStrategyState(StrategyState.Cancelled);
    }

    @Override
    public final void processStrategyCancelReject(StrategyCancelReject strategyCancelReject) {
        // update context
        context.updateOrderView(strategyCancelReject.getOrderView());
        context.setStrategyState(Working);
    }

    @Override
    public final void processStrategyUnsolicitedCancel(StrategyUnsolicitedCancel strategyUnsolicitedCancel) {
        // update context
        context.updateOrderView(strategyUnsolicitedCancel.getOrderView());
        context.setStrategyState(StrategyState.UnsolicitedCancel);
    }

    //////////////////////////////////////////////////
    // child order report
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyChildOrderNewAck(StrategyChildOrderNewAck strategyChildOrderNewAck) {
        // update context
        context.updateOrderView(strategyChildOrderNewAck.getOrderView());
        context.addChildOrder(strategyChildOrderNewAck.getChildOrderView());
        context.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderNewReject(StrategyChildOrderNewReject strategyChildOrderNewReject) {
        // update context
        context.updateOrderView(strategyChildOrderNewReject.getOrderView());
        context.updateChildOrder(strategyChildOrderNewReject.getChildOrderView());
        context.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderAmendAck(StrategyChildOrderAmendAck strategyChildOrderAmendAck) {
        // update context
        context.updateOrderView(strategyChildOrderAmendAck.getOrderView());
        context.updateChildOrder(strategyChildOrderAmendAck.getChildOrderView());
        context.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderAmendReject(StrategyChildOrderAmendReject strategyChildOrderAmendReject) {
        // update context
        context.updateOrderView(strategyChildOrderAmendReject.getOrderView());
        context.updateChildOrder(strategyChildOrderAmendReject.getChildOrderView());
        context.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderCancelAck(StrategyChildOrderCancelAck strategyChildOrderCancelAck) {
        // update context
        context.updateOrderView(strategyChildOrderCancelAck.getOrderView());
        context.removeChildOrder(strategyChildOrderCancelAck.getChildOrderView());
        context.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderCancelReject(StrategyChildOrderCancelReject strategyChildOrderCancelReject) {
        // update context
        context.updateOrderView(strategyChildOrderCancelReject.getOrderView());
        context.updateChildOrder(strategyChildOrderCancelReject.getChildOrderView());
        context.decrementOnTheWireOrdersCount();
    }

    @Override
    public final void processStrategyChildOrderExpire(StrategyChildOrderExpire strategyChildOrderExpire) {
        // update context
        context.updateOrderView(strategyChildOrderExpire.getOrderView());
        context.removeChildOrder(strategyChildOrderExpire.getChildOrderView());
    }

    @Override
    public final void processStrategyChildOrderUnsolicitedCancel(StrategyChildOrderUnsolicitedCancel strategyChildOrderUnsolicitedCancel) {
        // update context
        context.updateOrderView(strategyChildOrderUnsolicitedCancel.getOrderView());
        context.removeChildOrder(strategyChildOrderUnsolicitedCancel.getChildOrderView());
    }

    //////////////////////////////////////////////////
    // child order fill
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyChildOrderFill(StrategyChildOrderFill strategyChildOrderFill) {
        // update context
        context.updateOrderView(strategyChildOrderFill.getOrderView());
        if (context.getOrder().getOrderState() == OrderState.FullyFilled) context.setStrategyState(StrategyState.FullyFilled);
        if (strategyChildOrderFill.getChildOrderView().getOrderState() == OrderState.FullyFilled) {
            context.removeChildOrder(strategyChildOrderFill.getChildOrderView());
        } else {
            context.updateChildOrder(strategyChildOrderFill.getChildOrderView());
        }

        // send fill event to parent order
        context.getFillSender().sendFill(context.getOrder().getOrderId(), strategyChildOrderFill.getExecQuantity());
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
        context.onTimer(strategyTimerEvent.getTimerTag(), strategyTimerEvent.getTimerEventTime());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // strategy state processing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void processStrategyState() {
        switch (context.getStrategyState()) {
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
            doAction();
        }
    }

    private void processStrategyStateRejected() {
        terminate("new order is rejected.");
    }

    private void processStrategyStateFullyFilled() {
        terminate("new order is fully filled.");
    }

    private void processStrategyStateCancelled() {
        terminate("new order is cancelled.");
    }

    private void processStrategyStateUnsolicitedCancel() {
        terminate("new order is unsolicited cancelled.");
    }

    private void processStrategyStatePendingNew() {
        logger.trace("not doing anything.");
    }

    private void processStrategyStatePendingAmend() {
        logger.trace("processing pending amend state.");

        IStrategyPendingAmendProcessor pap = context.getStrategyPendingAmendProcessor();
        pap.process(context.getStrategyPendingAmendContext());

        if (pap.getResult() == PendingAmendCancelResult.Working) {
            return;
        }

        context.setStrategyState(StrategyState.PostAmend);
        context.getTimerRegistry().removeRepetitiveTimer(strategyParameters.getPendingAmendCancelRepetitiveTimerTag());

        if (pap.getResult() == PendingAmendCancelResult.Succeeded) {
            logger.info("PendingAmendProcessor succeeded.");
            context.getReportSender().sendAmendAck(context.getOrder().getOrderId(), pap.getRequestId());
        } else if (pap.getResult() == PendingAmendCancelResult.Failed) {
            logger.info("PendingAmendProcessor failed.");
            context.getReportSender().sendAmendReject(context.getOrder().getOrderId(), pap.getRequestId(), pap.getMessage());
        }
    }

    private void processStrategyStatePendingCancel() {
        logger.trace("processing pending cancel state.");

        IStrategyPendingCancelProcessor pcp = context.getStrategyPendingCancelProcessor();
        pcp.process();

        if (pcp.getResult() == PendingAmendCancelResult.Working) {
            return;
        }

        context.setStrategyState(StrategyState.PostCancel);
        context.getTimerRegistry().removeRepetitiveTimer(strategyParameters.getPendingAmendCancelRepetitiveTimerTag());

        if (pcp.getResult() == PendingAmendCancelResult.Succeeded) {
            logger.info("PendingCancelProcessor succeeded.");
            context.getReportSender().sendCancelAck(context.getOrder().getOrderId(), pcp.getRequestId());
        } else if (pcp.getResult() == PendingAmendCancelResult.Failed) {
            logger.info("PendingCancelProcessor failed.");
            context.getReportSender().sendCancelReject(context.getOrder().getOrderId(), pcp.getRequestId(), pcp.getMessage());
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
        if (context.getChildOrderContainer().hasOnTheWireChildOrders()) {
            logger.trace("cannot proceed since some on-the-wire child orders exist.");
            return false;
        }

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // utilities
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean isValid(List<IValidator> validators, Predicate<IValidator> predicate) {
        boolean isValid = true;
        for (IValidator validator : validators) {
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
