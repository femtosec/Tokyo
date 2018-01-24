package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.myogadani.ordermanagement.order.OrderState;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.StrategyDescriptor;
import jp.co.myogadanimotors.myogadani.strategy.context.*;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder.*;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorderfill.StrategyChildOrderFill;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.marketdata.StrategyMarketDataEvent;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.order.*;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.timer.StrategyTimerEvent;
import jp.co.myogadanimotors.myogadani.strategy.validator.IValidator;
import jp.co.myogadanimotors.myogadani.strategy.validator.RejectMessage;
import jp.co.myogadanimotors.myogadani.strategy.validator.StrategyStateValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractStrategy implements IStrategy {

    protected final Logger logger = LogManager.getLogger(getClass().getName());

    private final IStrategyDescriptor strategyDescriptor;
    private final StrategyContext context;
    private final List<IValidator> validators = new ArrayList<>();

    public AbstractStrategy(IStrategyDescriptor strategyDescriptor, StrategyContext context) {
        this.strategyDescriptor = new StrategyDescriptor(strategyDescriptor);
        this.context = context;
    }

    @Override
    public final IStrategyDescriptor getStrategyDescriptor() {
        return strategyDescriptor;
    }

    protected final void addValidator(IValidator validator) {
        validators.add(validator);
    }

    protected void init() {
        // add validators
        addValidator(new StrategyStateValidator());
    }

    protected void terminate(String message) {
        logger.info("terminating strategy. ({})", message);
    }

    protected IStrategyPendingAmendContext createPendingAmendContext() {
        return new StrategyPendingAmendContext();
    }

    protected IStrategyPendingAmendProcessor createPendingAmendProcessor(long requestId) {
        return new StrategyPendingAmendProcessor(requestId);
    }

    protected IStrategyPendingCancelProcessor createPendingCancelProcessor(long requestId) {
        return new StrategyPendingCancelProcessor(requestId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // process strategy event processing
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

        // validate report report request
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

        // create pending amend context
        IStrategyPendingAmendContext pac = createPendingAmendContext();


        // validate report amend report request
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validators, (validator) -> validator.isValidStrategyRequestAmend(strategyAmend, context, pac, rejectMessages))) {
            context.setStrategyState(StrategyState.PendingAmend);
            context.setStrategyPendingAmendProcessor(createPendingAmendProcessor(strategyAmend.getRequestId()));
        } else {
            context.getReportSender().sendAmendReject(context.getOrder().getOrderId(), strategyAmend.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    @Override
    public final void processStrategyCancel(StrategyCancel strategyCancel) {
        // update context
        context.updateOrderView(strategyCancel.getOrderView());

        // validate report cancel report request
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validators, (validator) -> validator.isValidStrategyRequestCancel(strategyCancel, context, rejectMessages))) {
            context.setStrategyState(StrategyState.PendingCancel);
            context.setStrategyPendingCancelProcessor(createPendingCancelProcessor(strategyCancel.getRequestId()));
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
        context.setStrategyState(StrategyState.Working);
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
        context.setStrategyState(StrategyState.Working);
    }

    @Override
    public final void processStrategyAmendReject(StrategyAmendReject strategyAmendReject) {
        // update context
        context.updateOrderView(strategyAmendReject.getOrderView());
        context.setStrategyState(StrategyState.Working);
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
        context.setStrategyState(StrategyState.Working);
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
    }

    @Override
    public final void processStrategyChildOrderNewReject(StrategyChildOrderNewReject strategyChildOrderNewReject) {
        // update context
        context.updateOrderView(strategyChildOrderNewReject.getOrderView());
    }

    @Override
    public final void processStrategyChildOrderAmendAck(StrategyChildOrderAmendAck strategyChildOrderAmendAck) {
        // update context
        context.updateOrderView(strategyChildOrderAmendAck.getOrderView());
    }

    @Override
    public final void processStrategyChildOrderAmendReject(StrategyChildOrderAmendReject strategyChildOrderAmendReject) {
        // update context
        context.updateOrderView(strategyChildOrderAmendReject.getOrderView());
    }

    @Override
    public final void processStrategyChildOrderCancelAck(StrategyChildOrderCancelAck strategyChildOrderCancelAck) {
        // update context
        context.updateOrderView(strategyChildOrderCancelAck.getOrderView());
    }

    @Override
    public final void processStrategyChildOrderCancelReject(StrategyChildOrderCancelReject strategyChildOrderCancelReject) {
        // update context
        context.updateOrderView(strategyChildOrderCancelReject.getOrderView());
    }

    @Override
    public final void processStrategyChildOrderExpire(StrategyChildOrderExpire strategyChildOrderExpire) {
        // update context
        context.updateOrderView(strategyChildOrderExpire.getOrderView());
    }

    @Override
    public final void processStrategyChildOrderUnsolicitedCancel(StrategyChildOrderUnsolicitedCancel strategyChildOrderUnsolicitedCancel) {
        // update context
        context.updateOrderView(strategyChildOrderUnsolicitedCancel.getOrderView());
    }

    //////////////////////////////////////////////////
    // child order fill
    //////////////////////////////////////////////////

    @Override
    public final void processStrategyChildOrderFill(StrategyChildOrderFill strategyChildOrderFill) {
        // update context
        context.updateOrderView(strategyChildOrderFill.getOrderView());
        if (context.getOrder().getOrderState() == OrderState.FullyFilled) context.setStrategyState(StrategyState.FullyFilled);

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
    // process strategy state
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void processStrategyState() {
        switch (context.getStrategyState()) {
            case Working:           processStrategyStateWorking(); break;
            case Rejected:          processStrategyStateRejected(); break;
            case FullyFilled:       processStrategyStateFullyFilled(); break;
            case Cancelled:         processStrategyStateCancelled(); break;
            case UnsolicitedCancel: processStrategyStateUnsolicitedCancel(); break;
            case PendingNew:        break; // not doing anything
            case PendingAmend:      processStrategyStatePendingAmend(); break;
            case PendingCancel:     processStrategyStatePendingCancel(); break;
            case PostAmend:         processStrategyStatePostAmend(); break;
            case PostCancel:        processStrategyStatePostCancel(); break;
        }
    }

    private void processStrategyStateWorking() {
        logger.trace("processing working state.");
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

    private void processStrategyStatePendingAmend() {
        logger.trace("processing pending amend.");

        IStrategyPendingAmendProcessor pap = context.getStrategyPendingAmendProcessor();
        pap.process(context.getStrategyPendingAmendContext());

        if (pap.getResult() == PendingAmendCancelResult.Working) {
            return;
        }

        context.setStrategyState(StrategyState.PostAmend);

        if (pap.getResult() == PendingAmendCancelResult.Succeeded) {
            logger.info("PendingAmendProcessor succeeded.");
            context.getReportSender().sendAmendAck(context.getOrder().getOrderId(), pap.getRequestId());
        } else if (pap.getResult() == PendingAmendCancelResult.Failed) {
            logger.info("PendingAmendProcessor failed.");
            context.getReportSender().sendAmendReject(context.getOrder().getOrderId(), pap.getRequestId(), pap.getMessage());
        }
    }

    private void processStrategyStatePendingCancel() {
        logger.trace("processing pending cancel.");

        IStrategyPendingCancelProcessor pcp = context.getStrategyPendingCancelProcessor();
        pcp.process();

        if (pcp.getResult() == PendingAmendCancelResult.Working) {
            return;
        }

        context.setStrategyState(StrategyState.PostCancel);

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
