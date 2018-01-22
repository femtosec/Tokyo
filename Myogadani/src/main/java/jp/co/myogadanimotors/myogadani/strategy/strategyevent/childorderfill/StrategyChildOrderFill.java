package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorderfill;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.AbstractStrategyEvent;

public final class StrategyChildOrderFill extends AbstractStrategyEvent {

    private final OrderView orderView;
    private final OrderView childOrderView;

    public StrategyChildOrderFill(long eventId,
                                  long creationTime,
                                  IStrategy strategy,
                                  OrderView orderView,
                                  OrderView childOrderView) {
        super(eventId, creationTime, strategy);
        this.orderView = orderView;
        this.childOrderView = childOrderView;
    }

    public final OrderView getOrderView() {
        return orderView;
    }

    public final OrderView getChildOrderView() {
        return childOrderView;
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyChildOrderFill(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderView: ").append(orderView)
                .append(", childOrderView: ").append(childOrderView);
    }
}
