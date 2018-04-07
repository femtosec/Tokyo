package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyCancelAck extends BaseStrategyReportEvent {

    public StrategyCancelAck(long eventId,
                             long creationTime,
                             IStrategyEventListener strategyEventListener,
                             OrderView orderView) {
        super(eventId, creationTime, strategyEventListener, orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.CancelAck;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyCancelAck(this);
    }
}
