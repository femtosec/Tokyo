package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.eventprocessing.order.neworder.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.neworder.Orderer;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

public final class StrategyCancel extends AbstractStrategyOrderEvent {

    public StrategyCancel(long eventId,
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
        strategy.processStrategyCancel(this);
    }
}
