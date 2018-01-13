package jp.co.myogadanimotors.myogadani.strategy.validator;

import jp.co.myogadanimotors.myogadani.strategy.context.IStrategyContext;
import jp.co.myogadanimotors.myogadani.strategy.context.IStrategyPendingAmendContext;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.order.StrategyAmend;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.order.StrategyCancel;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.order.StrategyNew;

import java.util.List;

public interface IValidator {

    /**
     * validates strategy new report
     */
    boolean isValidStrategyRequestNew(StrategyNew requestNew, IStrategyContext context, List<RejectMessage> rejectMessages);

    /**
     * validates strategy amend report
     */
    boolean isValidStrategyRequestAmend(StrategyAmend requestAmend, IStrategyContext context, IStrategyPendingAmendContext amendContext, List<RejectMessage> rejectMessages);

    /**
     * validates strategy cancel report
     */
    boolean isValidStrategyRequestCancel(StrategyCancel requestCancel, IStrategyContext context, List<RejectMessage> rejectMessages);
}
