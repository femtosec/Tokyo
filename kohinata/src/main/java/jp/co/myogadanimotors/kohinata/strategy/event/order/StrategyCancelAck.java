package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyCancelAck extends AbstractStrategyReportEvent {

    public StrategyCancelAck(long eventId,
                             long creationTime,
                             StrategyContext context,
                             OrderView orderView) {
        super(eventId, creationTime, context, orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.CancelAck;
    }

    @Override
    protected void callEventProcessor(StrategyContext context) {
        context.processStrategyCancelAck(this);
    }
}
