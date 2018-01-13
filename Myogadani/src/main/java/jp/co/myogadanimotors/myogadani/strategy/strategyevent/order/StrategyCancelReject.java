package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyCancelReject extends AbstractStrategyReportEvent {

    public StrategyCancelReject(OrderView orderView) {
        super(orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.CancelReject;
    }
}
