package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyNewAck extends BaseStrategyReportEvent {

    public StrategyNewAck(long eventId,
                          long creationTime,
                          IStrategyEventListener strategyEventListener,
                          OrderView orderView) {
        super(eventId, creationTime, strategyEventListener, orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.NewAck;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyNewAck(this);
    }
}
