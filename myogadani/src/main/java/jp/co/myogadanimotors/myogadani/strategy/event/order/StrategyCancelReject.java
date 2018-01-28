package jp.co.myogadanimotors.myogadani.strategy.event.order;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyCancelReject extends AbstractStrategyReportEvent {

    private final String message;

    public StrategyCancelReject(long eventId,
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
        strategy.processStrategyCancelReject(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", message: ").append(message);
    }
}
