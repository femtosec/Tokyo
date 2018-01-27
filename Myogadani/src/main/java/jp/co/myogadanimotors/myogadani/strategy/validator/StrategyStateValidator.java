package jp.co.myogadanimotors.myogadani.strategy.validator;

import jp.co.myogadanimotors.myogadani.strategy.StrategyState;
import jp.co.myogadanimotors.myogadani.strategy.context.IStrategyContext;
import jp.co.myogadanimotors.myogadani.strategy.context.IStrategyPendingAmendContext;
import jp.co.myogadanimotors.myogadani.strategy.event.order.StrategyAmend;
import jp.co.myogadanimotors.myogadani.strategy.event.order.StrategyCancel;
import jp.co.myogadanimotors.myogadani.strategy.event.order.StrategyNew;

import java.util.List;

public final class StrategyStateValidator implements IValidator {

    private final RejectMessage STRATEGY_STATE_INVALID_ON_REQUEST_NEW = new RejectMessage("new report request must be sent during PendingNew");
    private final RejectMessage STRATEGY_STATE_INVALID_ON_REQUEST_AMEND = new RejectMessage("amend report request must be sent during Working");
    private final RejectMessage STRATEGY_STATE_INVALID_ON_REQUEST_CANCEL = new RejectMessage("cancel report request must be sent during Working");

    @Override
    public boolean isValidStrategyRequestNew(StrategyNew requestNew, IStrategyContext context, List<RejectMessage> rejectMessages) {
        if (context.getStrategyState() != StrategyState.PendingNew) {
            rejectMessages.add(STRATEGY_STATE_INVALID_ON_REQUEST_NEW);
            return false;
        }
        return true;
    }

    @Override
    public boolean isValidStrategyRequestAmend(StrategyAmend requestAmend, IStrategyContext context, IStrategyPendingAmendContext amendContext, List<RejectMessage> rejectMessages) {
        if (context.getStrategyState() != StrategyState.Working) {
            rejectMessages.add(STRATEGY_STATE_INVALID_ON_REQUEST_AMEND);
            return false;
        }
        return true;
    }

    @Override
    public boolean isValidStrategyRequestCancel(StrategyCancel requestCancel, IStrategyContext context, List<RejectMessage> rejectMessages) {
        if (context.getStrategyState() != StrategyState.Working) {
            rejectMessages.add(STRATEGY_STATE_INVALID_ON_REQUEST_CANCEL);
            return false;
        }
        return true;
    }
}
