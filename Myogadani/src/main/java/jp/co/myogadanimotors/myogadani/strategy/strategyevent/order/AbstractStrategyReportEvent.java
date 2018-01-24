package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.AbstractStrategyEvent;

import static jp.co.myogadanimotors.myogadani.common.Utility.notNull;

public abstract class AbstractStrategyReportEvent extends AbstractStrategyEvent {

    private final OrderView orderView;

    public AbstractStrategyReportEvent(long eventId,
                                       long creationTime,
                                       IStrategy strategy,
                                       OrderView orderView) {
        super(eventId, creationTime, strategy);
        this.orderView = notNull(orderView);
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
