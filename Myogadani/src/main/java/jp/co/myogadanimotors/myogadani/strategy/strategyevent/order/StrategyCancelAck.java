package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyCancelAck extends AbstractStrategyReportEvent {

    public StrategyCancelAck(long eventId,
                             long creationTime,
                             IStrategy strategy,
                             OrderView orderView) {
        super(eventId, creationTime, strategy, orderView);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyCancelAck(this);
    }
}
