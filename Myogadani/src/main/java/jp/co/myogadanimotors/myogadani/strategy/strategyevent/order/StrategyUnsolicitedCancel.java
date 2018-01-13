package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public class StrategyUnsolicitedCancel extends AbstractStrategyReportEvent {

    public StrategyUnsolicitedCancel(OrderView orderView) {
        super(orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.UnsolicitedCancel;
    }
}
