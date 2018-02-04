package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;

public final class StrategyAmendAck extends AbstractStrategyReportEvent {

    public StrategyAmendAck(long eventId,
                            long creationTime,
                            IStrategy strategy,
                            OrderView orderView) {
        super(eventId, creationTime, strategy, orderView);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyAmendAck(this);
    }
}
