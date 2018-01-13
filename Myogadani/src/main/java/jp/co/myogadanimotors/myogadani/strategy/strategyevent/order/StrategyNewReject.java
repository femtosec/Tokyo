package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyNewReject extends AbstractStrategyReportEvent {

    public StrategyNewReject(OrderView orderView) {
        super(orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.NewReject;
    }
}
