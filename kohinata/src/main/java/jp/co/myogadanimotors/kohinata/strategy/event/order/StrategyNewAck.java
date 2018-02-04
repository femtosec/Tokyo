package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;

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
