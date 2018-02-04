package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;

public final class StrategyAmendReject extends AbstractStrategyReportEvent {

    private final String message;

    public StrategyAmendReject(long eventId,
                               long creationTime,
                               IStrategy strategy,
                               OrderView orderView,
                               String message) {
        super(eventId, creationTime, strategy, orderView);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyAmendReject(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", message: ").append(message);
    }
}
