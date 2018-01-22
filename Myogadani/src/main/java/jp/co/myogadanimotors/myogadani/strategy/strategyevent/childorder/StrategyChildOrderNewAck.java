package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyChildOrderNewAck extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderNewAck(long eventId,
                                    long creationTime,
                                    IStrategy strategy,
                                    OrderView orderView,
                                    OrderView childOrderView) {
        super(eventId, creationTime, strategy, orderView, childOrderView, null);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyChildOrderNewAck(this);
    }
}
