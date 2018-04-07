package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyCancelReject extends BaseStrategyReportEvent {

    private final String message;

    public StrategyCancelReject(long eventId,
                                long creationTime,
                                IStrategyEventListener strategyEventListener,
                                OrderView orderView,
                                String message) {
        super(eventId, creationTime, strategyEventListener, orderView);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.CancelReject;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyCancelReject(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", message: ").append(message);
    }
}
