package jp.co.myogadanimotors.kohinata.strategy.event.order;

import jp.co.myogadanimotors.kohinata.strategy.context.OrderView;
import jp.co.myogadanimotors.kohinata.strategy.event.IStrategyEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.StrategyEventType;

public final class StrategyAmendAck extends BaseStrategyReportEvent {

    public StrategyAmendAck(long eventId,
                            long creationTime,
                            IStrategyEventListener strategyEventListener,
                            OrderView orderView) {
        super(eventId, creationTime, strategyEventListener, orderView);
    }

    @Override
    public StrategyEventType getStrategyEventType() {
        return StrategyEventType.AmendAck;
    }

    @Override
    protected void callStrategyEventListener(IStrategyEventListener strategyEventListener) {
        strategyEventListener.processStrategyAmendAck(this);
    }
}
