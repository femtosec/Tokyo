package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.myogadani.strategy.context.IStrategyPendingAmendContext;
import jp.co.myogadanimotors.myogadani.strategy.context.IStrategyPendingAmendProcessor;
import jp.co.myogadanimotors.myogadani.strategy.context.IStrategyPendingCancelProcessor;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
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

    protected final Logger log = LogManager.getLogger(getClass().getName());

    private final int strategyTypeId;
    private final StrategyContext context;
    private final List<IValidator> validators = new ArrayList<>();

    public AbstractStrategy(int strategyTypeId, StrategyContext context) {
        this.strategyTypeId = strategyTypeId;
        this.context = context;
    }

    @Override
    public final int getStrategyTypeId() {
        return strategyTypeId;
    }

    protected final void addValidator(IValidator validator) {
        validators.add(validator);
    }

    protected void init() {
        // add validators
        addValidator(new StrategyStateValidator());
    }

    protected IStrategyPendingAmendContext createPendingAmendContext() {
        // todo: to be implemented
        return null;
    }

    protected IStrategyPendingAmendProcessor createPendingAmendProcessor(IStrategyPendingAmendContext pendingAmendContext) {
        // todo: to be implemented
        return null;
    }

    protected IStrategyPendingCancelProcessor createPendingCancelProcessor() {
        // todo: to be implemented
        return null;
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
    public void processStrategyNew(StrategyNew strategyNew) {
        // update context
        context.setOrder(strategyNew.getOrderView());

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
    public void processStrategyAmend(StrategyAmend strategyAmend) {
        // update context
        context.setOrder(strategyAmend.getOrderView());

        // create pending amend context
        IStrategyPendingAmendContext pac = createPendingAmendContext();

        // validate report amend report request
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validators, (validator) -> validator.isValidStrategyRequestAmend(strategyAmend, context, pac, rejectMessages))) {
            context.setStrategyState(StrategyState.PendingAmend);
            context.setStrategyPendingAmendProcessor(createPendingAmendProcessor(pac));
        } else {
            context.getReportSender().sendAmendReject(context.getOrder().getOrderId(), strategyAmend.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    @Override
    public void processStrategyCancel(StrategyCancel strategyCancel) {
        // update context
        context.setOrder(strategyCancel.getOrderView());

        // validate report cancel report request
        List<RejectMessage> rejectMessages = new ArrayList<>();
        if (isValid(validators, (validator) -> validator.isValidStrategyRequestCancel(strategyCancel, context, rejectMessages))) {
            context.setStrategyState(StrategyState.PendingCancel);
            context.setStrategyPendingCancelProcessor(createPendingCancelProcessor());
        } else {
            context.getReportSender().sendCancelReject(context.getOrder().getOrderId(), strategyCancel.getRequestId(), combineRejectMessages(rejectMessages));
        }
    }

    //////////////////////////////////////////////////
    // order report event
    //////////////////////////////////////////////////

    @Override
    public void processStrategyNewAck(StrategyNewAck strategyNewAck) {
        // update context
        context.setOrder(strategyNewAck.getOrderView());
    }

    @Override
    public void processStrategyNewReject(StrategyNewReject strategyNewReject) {
        // update context
        context.setOrder(strategyNewReject.getOrderView());
    }

    @Override
    public void processStrategyAmendAck(StrategyAmendAck strategyAmendAck) {
        // update context
        context.setOrder(strategyAmendAck.getOrderView());
    }

    @Override
    public void processStrategyAmendReject(StrategyAmendReject strategyAmendReject) {
        // update context
        context.setOrder(strategyAmendReject.getOrderView());
    }

    @Override
    public void processStrategyCancelAck(StrategyCancelAck strategyCancelAck) {
        // update context
        context.setOrder(strategyCancelAck.getOrderView());
    }

    @Override
    public void processStrategyCancelReject(StrategyCancelReject strategyCancelReject) {
        // update context
        context.setOrder(strategyCancelReject.getOrderView());
    }

    @Override
    public void processStrategyUnsolicitedCancel(StrategyUnsolicitedCancel strategyUnsolicitedCancel) {
        // update context
        context.setOrder(strategyUnsolicitedCancel.getOrderView());
    }

    //////////////////////////////////////////////////
    // child order report
    //////////////////////////////////////////////////

    @Override
    public void processStrategyChildOrderNewAck(StrategyChildOrderNewAck strategyChildOrderNewAck) {

    }

    @Override
    public void processStrategyChildOrderNewReject(StrategyChildOrderNewReject strategyChildOrderNewReject) {

    }

    @Override
    public void processStrategyChildOrderAmendAck(StrategyChildOrderAmendAck strategyChildOrderAmendAck) {

    }

    @Override
    public void processStrategyChildOrderAmendReject(StrategyChildOrderAmendReject strategyChildOrderAmendReject) {

    }

    @Override
    public void processStrategyChildOrderCancelAck(StrategyChildOrderCancelAck strategyChildOrderCancelAck) {

    }

    @Override
    public void processStrategyChildOrderCancelReject(StrategyChildOrderCancelReject strategyChildOrderCancelReject) {

    }

    @Override
    public void processStrategyChildOrderExpire(StrategyChildOrderExpire strategyChildOrderExpire) {

    }

    @Override
    public void processStrategyChildOrderUnsolicitedCancel(StrategyChildOrderUnsolicitedCancel strategyChildOrderUnsolicitedCancel) {

    }

    //////////////////////////////////////////////////
    // child order fill
    //////////////////////////////////////////////////

    @Override
    public void processStrategyChildOrderFill(StrategyChildOrderFill strategyChildOrderFill) {
        // todo: report fill to parent order
    }

    //////////////////////////////////////////////////
    // market data event
    //////////////////////////////////////////////////

    @Override
    public void processStrategyMarketDataEvent(StrategyMarketDataEvent strategyMarketDataEvent) {

    }

    //////////////////////////////////////////////////
    // timer event event
    //////////////////////////////////////////////////

    @Override
    public void processStrategyTimerEvent(StrategyTimerEvent strategyTimerEvent) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // process strategy state
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void processStrategyState() {
        switch (context.getStrategyState()) {
            case Working:           processStrategyStateWorking(); break;
            case Rejected:          processStrategyStateReject(); break;
            case FullyFilled:       processStrategyStateFullyFilled(); break;
            case Cancelled:         processStrategyStateCancelled(); break;
            case UnsolicitedCancel: processStrategyStateUnsolicitedCancel(); break;
            case PendingNew:        break; // not doing anything
            case PendingAmend:      processStrategyStatePendingAmend(); break;
            case PendingCancel:     processStrategyStatePendingCancel(); break;
        }
    }

    private void processStrategyStateWorking() {

    }

    private void processStrategyStateReject() {

    }

    private void processStrategyStateFullyFilled() {

    }

    private void processStrategyStateCancelled() {

    }

    private void processStrategyStateUnsolicitedCancel() {

    }

    private void processStrategyStatePendingAmend() {

    }

    private void processStrategyStatePendingCancel() {

    }

    private void checkFullyFilled() {
        // todo: if report is fully filled, change strategy state to "FullyFilled"
    }

    private void checkSpamming() {
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
