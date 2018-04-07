package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.event.order.OrderDestination;
import jp.co.myogadanimotors.kohinata.event.order.Orderer;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyNew extends BaseStrategyOrderEvent {

    public StrategyNew(long eventId,
                       long creationTime,
                       IStrategyEventListener strategyEventListener,
                       long requestId,
                       OrderView orderView,
                       Orderer orderer,
                       OrderDestination destination) {
        super(eventId, creationTime, strategyEventListener, requestId, orderView, orderer, destination);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.New;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyNew(this);
    }
}
