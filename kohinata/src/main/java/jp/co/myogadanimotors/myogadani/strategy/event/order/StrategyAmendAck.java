package jp.co.myogadanimotors.myogadani.strategy.event.order;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

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
