package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyAmendReject extends AbstractStrategyReportEvent {

    private final String message;

    public StrategyAmendReject(long eventId,
                               long creationTime,
                               StrategyContext context,
                               OrderView orderView,
                               String message) {
        super(eventId, creationTime, context, orderView);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.AmendReject;
    }

    @Override
    protected void callEventProcessor(StrategyContext context) {
        context.processStrategyAmendReject(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", message: ").append(message);
    }
}
