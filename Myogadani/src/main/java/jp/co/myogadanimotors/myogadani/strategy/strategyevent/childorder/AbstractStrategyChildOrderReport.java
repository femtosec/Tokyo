package jp.co.myogadanimotors.myogadani.strategy.strategyevent.childorder;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.AbstractStrategyEvent;

public abstract class AbstractStrategyChildOrderReport extends AbstractStrategyEvent {

    private final OrderView orderView;
    private final OrderView childOrderView;
    private final String childOrderTag;
    private final String message;

    public AbstractStrategyChildOrderReport(long eventId,
                                            long creationTime,
                                            IStrategy strategy,
                                            OrderView orderView,
                                            OrderView childOrderView,
                                            String childOrderTag,
                                            String message) {
        super(eventId, creationTime, strategy);
        this.orderView = orderView;
        this.childOrderView = childOrderView;
        this.childOrderTag = childOrderTag;
        this.message = message;
    }

    public final OrderView getOrderView() {
        return orderView;
    }

    public final OrderView getChildOrderView() {
        return childOrderView;
    }

    public final String getChildOrderTag() {
        return childOrderTag;
    }

    public final String getMessage() {
        return message;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderView: ").append(orderView)
                .append(", childOrderView: ").append(childOrderView)
                .append(", childOrderTag: ").append(childOrderTag)
                .append(", message: ").append(message);
    }
}
