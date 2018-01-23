package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyUnsolicitedCancel extends AbstractStrategyReportEvent {

    private final String message;

    public StrategyUnsolicitedCancel(long eventId,
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
        strategy.processStrategyUnsolicitedCancel(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", message: ").append(message);
    }
}
