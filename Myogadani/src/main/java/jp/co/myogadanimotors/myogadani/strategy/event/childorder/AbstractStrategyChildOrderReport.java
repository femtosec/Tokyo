package jp.co.myogadanimotors.myogadani.strategy.event.childorder;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.event.AbstractStrategyEvent;

import static jp.co.myogadanimotors.myogadani.common.Utility.notNull;

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
        this.orderView = notNull(orderView);
        this.childOrderView = notNull(childOrderView);
        this.childOrderTag = notNull(childOrderTag);
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
