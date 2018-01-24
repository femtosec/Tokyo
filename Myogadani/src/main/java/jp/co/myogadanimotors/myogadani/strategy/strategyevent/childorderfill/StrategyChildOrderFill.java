package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorderfill;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.AbstractStrategyEvent;

import java.math.BigDecimal;

import static jp.co.myogadanimotors.myogadani.common.Utility.notNull;

public final class StrategyChildOrderFill extends AbstractStrategyEvent {

    private final BigDecimal execQuantity;
    private final OrderView orderView;
    private final OrderView childOrderView;
    private final String childOrderTag;

    public StrategyChildOrderFill(long eventId,
                                  long creationTime,
                                  IStrategy strategy,
                                  BigDecimal execQuantity,
                                  OrderView orderView,
                                  OrderView childOrderView,
                                  String childOrderTag) {
        super(eventId, creationTime, strategy);
        this.execQuantity = execQuantity;
        this.orderView = notNull(orderView);
        this.childOrderView = notNull(childOrderView);
        this.childOrderTag = childOrderTag;
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

    public String getChildOrderTag() {
        return childOrderTag;
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
                .append(", childOrderView: ").append(childOrderView)
                .append(", childOrderTag: ").append(childOrderTag);
    }
}
