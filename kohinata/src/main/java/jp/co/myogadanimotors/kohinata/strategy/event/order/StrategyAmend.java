package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.event.order.OrderDestination;
import jp.co.myogadanimotors.kohinata.event.order.Orderer;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;

public final class StrategyAmend extends AbstractStrategyOrderEvent {

    private final OrderView amendOrderView;

    public StrategyAmend(long eventId,
                         long creationTime,
                         IStrategy strategy,
                         long requestId,
                         OrderView orderView,
                         OrderView amendOrderView,
                         Orderer orderer,
                         OrderDestination destination) {
        super(eventId, creationTime, strategy, requestId, orderView, orderer, destination);
        this.amendOrderView = amendOrderView;
    }

    public IOrder getAmendOrderView() {
        return amendOrderView;
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyAmend(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", amendOrderView: ").append(amendOrderView);
    }
}
