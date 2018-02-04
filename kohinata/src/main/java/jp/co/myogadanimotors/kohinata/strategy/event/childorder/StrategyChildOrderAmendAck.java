package jp.co.myogadanimotors.kohinata.strategy.event.childorder;

import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;

public final class StrategyChildOrderAmendAck extends AbstractStrategyChildOrderReport {

    public StrategyChildOrderAmendAck(long eventId,
                                      long creationTime,
                                      IStrategy strategy,
                                      OrderView orderView,
                                      OrderView childOrderView,
                                      String childOrderTag) {
        super(eventId, creationTime, strategy, orderView, childOrderView, childOrderTag, null);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyChildOrderAmendAck(this);
    }
}
