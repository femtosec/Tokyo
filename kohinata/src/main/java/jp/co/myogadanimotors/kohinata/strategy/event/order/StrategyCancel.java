package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.event.order.OrderDestination;
import jp.co.myogadanimotors.kohinata.event.order.Orderer;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyCancel extends AbstractStrategyOrderEvent {

    public StrategyCancel(long eventId,
                          long creationTime,
                          StrategyContext context,
                          long requestId,
                          OrderView orderView,
                          Orderer orderer,
                          OrderDestination destination) {
        super(eventId, creationTime, context, requestId, orderView, orderer, destination);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.Cancel;
    }

    @Override
    protected void callEventProcessor(StrategyContext context) {
        context.processStrategyCancel(this);
    }
}
