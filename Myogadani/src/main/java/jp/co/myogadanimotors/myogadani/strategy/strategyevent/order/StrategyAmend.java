package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyAmend extends AbstractStrategyOrderEvent {

    private final OrderView amendOrderView;

    public StrategyAmend(long requestId, OrderView orderView, OrderView amendOrderView, Orderer orderer, OrderDestination destination) {
        super(requestId, orderView, orderer, destination);
        this.amendOrderView = amendOrderView;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.Amend;
    }

    public IOrder getAmendOrderView() {
        return amendOrderView;
    }
}
