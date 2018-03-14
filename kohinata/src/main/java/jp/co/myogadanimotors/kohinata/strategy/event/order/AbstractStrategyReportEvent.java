package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.event.AbstractStrategyEvent;

import static java.util.Objects.requireNonNull;

public abstract class AbstractStrategyReportEvent extends AbstractStrategyEvent {

    private final OrderView orderView;

    public AbstractStrategyReportEvent(long eventId,
                                       long creationTime,
                                       StrategyContext context,
                                       OrderView orderView) {
        super(eventId, creationTime, context);
        this.orderView = requireNonNull(orderView);
    }

    public OrderView getOrderView() {
        return orderView;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderView: ").append(orderView);
    }
}
