package jp.co.myogadanimotors.myogadani.strategy.event.order;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyNewReject extends AbstractStrategyReportEvent {

    private final String message;

    public StrategyNewReject(long eventId,
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
        strategy.processStrategyNewReject(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", message: ").append(message);
    }
}
