package jp.co.myogadanimotors.kohinata.strategy.event.childorder;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.BaseStrategyEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;

import static java.util.Objects.requireNonNull;

public abstract class BaseStrategyChildOrderReport extends BaseStrategyEvent {

    private final OrderView orderView;
    private final OrderView childOrderView;
    private final String childOrderTag;
    private final String message;

    public BaseStrategyChildOrderReport(long eventId,
                                        long creationTime,
                                        IStrategyEventListener eventListener,
                                        OrderView orderView,
                                        OrderView childOrderView,
                                        String childOrderTag,
                                        String message) {
        super(eventId, creationTime, eventListener);
        this.orderView = requireNonNull(orderView);
        this.childOrderView = requireNonNull(childOrderView);
        this.childOrderTag = requireNonNull(childOrderTag);
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
