package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.kohinata.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.strategy.event.childorder.*;
import jp.co.myogadanimotors.myogadani.strategy.event.childorderfill.StrategyChildOrderFill;
import jp.co.myogadanimotors.myogadani.strategy.event.marketdata.StrategyMarketDataEvent;
import jp.co.myogadanimotors.myogadani.strategy.event.order.*;
import jp.co.myogadanimotors.myogadani.strategy.event.timer.StrategyTimerEvent;

public interface IStrategy {
    IStrategyDescriptor getStrategyDescriptor();

    void preProcessEvent();

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
