package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorderfill;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.AbstractStrategyEvent;

import java.math.BigDecimal;

public final class StrategyChildOrderFill extends AbstractStrategyEvent {

    private final BigDecimal execQuantity;
    private final OrderView orderView;
    private final OrderView childOrderView;

    public StrategyChildOrderFill(long eventId,
                                  long creationTime,
                                  IStrategy strategy,
                                  BigDecimal execQuantity,
                                  OrderView orderView,
                                  OrderView childOrderView) {
        super(eventId, creationTime, strategy);
        this.execQuantity = execQuantity;
        this.orderView = orderView;
        this.childOrderView = childOrderView;
    }

    public BigDecimal getExecQuantity() {
        return execQuantity;
    }

    public OrderView getOrderView() {
        return orderView;
    }

    public OrderView getChildOrderView() {
        return childOrderView;
    }

    @Override
    protected void callEventListener(IStrategy strategy) {
        strategy.processStrategyChildOrderFill(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", execQuantity: ").append(execQuantity)
                .append(", orderView: ").append(orderView)
                .append(", childOrderView: ").append(childOrderView);
    }
}
