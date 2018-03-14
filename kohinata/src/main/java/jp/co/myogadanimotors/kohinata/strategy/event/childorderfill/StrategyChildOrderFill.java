package jp.co.myogadanimotors.kohinata.strategy.event.childorderfill;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.AbstractStrategyEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public final class StrategyChildOrderFill extends AbstractStrategyEvent {

    private final BigDecimal execQuantity;
    private final OrderView orderView;
    private final OrderView childOrderView;
    private final String childOrderTag;

    public StrategyChildOrderFill(long eventId,
                                  long creationTime,
                                  StrategyContext context,
                                  BigDecimal execQuantity,
                                  OrderView orderView,
                                  OrderView childOrderView,
                                  String childOrderTag) {
        super(eventId, creationTime, context);
        this.execQuantity = execQuantity;
        this.orderView = requireNonNull(orderView);
        this.childOrderView = requireNonNull(childOrderView);
        this.childOrderTag = childOrderTag;
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.ChildOrderFill;
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
    protected void callEventProcessor(StrategyContext context) {
        context.processStrategyChildOrderFill(this);
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
