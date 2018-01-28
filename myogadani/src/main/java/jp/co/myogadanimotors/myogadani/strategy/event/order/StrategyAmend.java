package jp.co.myogadanimotors.myogadani.strategy.event.order;

import jp.co.myogadanimotors.myogadani.event.order.OrderDestination;
import jp.co.myogadanimotors.myogadani.event.order.Orderer;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;

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
