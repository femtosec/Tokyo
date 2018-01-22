package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public class StrategyChildOrderUnsolicitedCancel extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderUnsolicitedCancel(long eventId,
                                               long creationTime,
                                               IStrategy strategy,
                                               OrderView orderView,
                                               OrderView childOrderView,
                                               String message) {
        super(eventId, creationTime, strategy, orderView, childOrderView, message);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyChildOrderUnsolicitedCancel(this);
    }
}
