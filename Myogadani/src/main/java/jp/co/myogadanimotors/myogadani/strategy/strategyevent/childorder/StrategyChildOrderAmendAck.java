package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyChildOrderAmendAck extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderAmendAck(long eventId,
                                      long creationTime,
                                      IStrategy strategy,
                                      OrderView orderView,
                                      OrderView childOrderView) {
        super(eventId, creationTime, strategy, orderView, childOrderView, null);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyChildOrderAmendAck(this);
    }
}
