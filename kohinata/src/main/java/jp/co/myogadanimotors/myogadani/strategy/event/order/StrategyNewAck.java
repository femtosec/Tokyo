package jp.co.myogadanimotors.myogadani.strategy.event.order;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyNewAck extends AbstractStrategyReportEvent {

    public StrategyNewAck(long eventId,
                          long creationTime,
                          IStrategy strategy,
                          OrderView orderView) {
        super(eventId, creationTime, strategy, orderView);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyNewAck(this);
    }
}
