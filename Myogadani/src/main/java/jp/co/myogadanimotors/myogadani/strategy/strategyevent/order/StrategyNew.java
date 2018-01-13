package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyNew extends AbstractStrategyOrderEvent {

    public StrategyNew(long requestId, OrderView orderView, Orderer orderer, OrderDestination destination) {
        super(requestId, orderView, orderer, destination);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.New;
    }
}
