package jp.co.myogadanimotors.kohinata.strategy.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.kohinata.strategy.event.childorder.*;
import jp.co.myogadanimotors.kohinata.strategy.event.childorderfill.StrategyChildOrderFill;
import jp.co.myogadanimotors.kohinata.strategy.event.marketdata.StrategyMarketDataEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.order.*;
import jp.co.myogadanimotors.kohinata.strategy.event.timer.StrategyTimerEvent;

public interface IStrategyEventListener extends IAsyncEventListener {
    void preProcessEvent(BaseStrategyEvent strategyEvent);

    void postProcessEvent();

    void processStrategyNew(StrategyNew strategyNew);

    void processStrategyAmend(StrategyAmend strategyAmend);

    void processStrategyCancel(StrategyCancel strategyCancel);

    void processStrategyNewAck(StrategyNewAck strategyNewAck);

    void processStrategyNewReject(StrategyNewReject strategyNewReject);

    void processStrategyAmendAck(StrategyAmendAck strategyAmendAck);

    void processStrategyAmendReject(StrategyAmendReject strategyAmendReject);

    void processStrategyCancelAck(StrategyCancelAck strategyCancelAck);

    void processStrategyCancelReject(StrategyCancelReject strategyCancelReject);

    void processStrategyUnsolicitedCancel(StrategyUnsolicitedCancel strategyUnsolicitedCancel);

    void processStrategyChildOrderNewAck(StrategyChildOrderNewAck strategyChildOrderNewAck);

    void processStrategyChildOrderNewReject(StrategyChildOrderNewReject strategyChildOrderNewReject);

    void processStrategyChildOrderAmendAck(StrategyChildOrderAmendAck strategyChildOrderAmendAck);

    void processStrategyChildOrderAmendReject(StrategyChildOrderAmendReject strategyChildOrderAmendReject);

    void processStrategyChildOrderCancelAck(StrategyChildOrderCancelAck strategyChildOrderCancelAck);

    void processStrategyChildOrderCancelReject(StrategyChildOrderCancelReject strategyChildOrderCancelReject);

    void processStrategyChildOrderExpire(StrategyChildOrderExpire strategyChildOrderExpire);

    void processStrategyChildOrderUnsolicitedCancel(StrategyChildOrderUnsolicitedCancel strategyChildOrderUnsolicitedCancel);

    void processStrategyChildOrderFill(StrategyChildOrderFill strategyChildOrderFill);

    void processStrategyMarketDataEvent(StrategyMarketDataEvent strategyMarketDataEvent);

    void processStrategyTimerEvent(StrategyTimerEvent strategyTimerEvent);
}
