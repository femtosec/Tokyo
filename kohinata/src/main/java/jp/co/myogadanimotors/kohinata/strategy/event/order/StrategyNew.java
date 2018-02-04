package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.event.order.OrderDestination;
import jp.co.myogadanimotors.kohinata.event.order.Orderer;
import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;

public final class StrategyNew extends AbstractStrategyOrderEvent {

    public StrategyNew(long eventId,
                       long creationTime,
                       IStrategy strategy,
                       long requestId,
                       OrderView orderView,
                       Orderer orderer,
                       OrderDestination destination) {
        super(eventId, creationTime, strategy, requestId, orderView, orderer, destination);
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyNew(this);
    }
}
