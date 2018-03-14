package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyNewAck extends AbstractStrategyReportEvent {

    public StrategyNewAck(long eventId,
                          long creationTime,
                          StrategyContext context,
                          OrderView orderView) {
        super(eventId, creationTime, context, orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.NewAck;
    }

    @Override
    protected void callEventProcessor(StrategyContext context) {
        context.processStrategyNewAck(this);
    }
}
