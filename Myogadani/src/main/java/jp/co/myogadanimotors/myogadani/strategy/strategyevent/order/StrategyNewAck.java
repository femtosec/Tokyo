package jp.co.myogadanimotors.myogadani.strategy.strategyevent.order;

import jp.co.myogadanimotors.myogadani.strategy.context.OrderView;
import jp.co.myogadanimotors.myogadani.strategy.strategyevent.StrategyEventType;

public final class StrategyNewAck extends AbstractStrategyReportEvent {

    public StrategyNewAck(OrderView orderView) {
        super(orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.NewAck;
    }
}
