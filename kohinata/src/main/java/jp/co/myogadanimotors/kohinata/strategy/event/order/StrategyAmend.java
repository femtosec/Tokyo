package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.event.order.OrderDestination;
import jp.co.myogadanimotors.kohinata.event.order.Orderer;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyAmend extends BaseStrategyOrderEvent {

    private final OrderView amendOrderView;

    public StrategyAmend(long eventId,
                         long creationTime,
                         StrategyContext context,
                         long requestId,
                         OrderView orderView,
                         OrderView amendOrderView,
                         Orderer orderer,
                         OrderDestination destination) {
        super(eventId, creationTime, context, requestId, orderView, orderer, destination);
        this.amendOrderView = amendOrderView;
    }

    public IOrder getAmendOrderView() {
        return amendOrderView;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.Amend;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyAmend(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", amendOrderView: ").append(amendOrderView);
    }
}
