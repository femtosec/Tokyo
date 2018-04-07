package jp.co.myogadanimotors.kohinata.strategy.event;

import jp.co.myogadanimotors.kohinata.strategy.event.childorder.*;
import jp.co.myogadanimotors.kohinata.strategy.event.childorderfill.StrategyChildOrderFill;
import jp.co.myogadanimotors.kohinata.strategy.event.marketdata.StrategyMarketDataEvent;
import jp.co.myogadanimotors.kohinata.strategy.event.order.*;
import jp.co.myogadanimotors.kohinata.strategy.event.timer.StrategyTimerEvent;

import java.util.concurrent.Executor;

public class StrategyEventProcessor implements IStrategyEventListener {
    @Override
    public void preProcessEvent(BaseStrategyEvent strategyEvent) {

    }

    @Override
    public void postProcessEvent() {

    }

    @Override
    public void processStrategyNew(StrategyNew strategyNew) {

    }

    @Override
    public void processStrategyAmend(StrategyAmend strategyAmend) {

    }

    @Override
    public void processStrategyCancel(StrategyCancel strategyCancel) {

    }

    @Override
    public void processStrategyNewAck(StrategyNewAck strategyNewAck) {

    }

    @Override
    public void processStrategyNewReject(StrategyNewReject strategyNewReject) {

    }

    @Override
    public void processStrategyAmendAck(StrategyAmendAck strategyAmendAck) {

    }

    @Override
    public void processStrategyAmendReject(StrategyAmendReject strategyAmendReject) {

    }

    @Override
    public void processStrategyCancelAck(StrategyCancelAck strategyCancelAck) {

    }

    @Override
    public void processStrategyCancelReject(StrategyCancelReject strategyCancelReject) {

    }

    @Override
    public void processStrategyUnsolicitedCancel(StrategyUnsolicitedCancel strategyUnsolicitedCancel) {

    }

    @Override
    public void processStrategyChildOrderNewAck(StrategyChildOrderNewAck strategyChildOrderNewAck) {

    }

    @Override
    public void processStrategyChildOrderNewReject(StrategyChildOrderNewReject strategyChildOrderNewReject) {

    }

    @Override
    public void processStrategyChildOrderAmendAck(StrategyChildOrderAmendAck strategyChildOrderAmendAck) {

    }

    @Override
    public void processStrategyChildOrderAmendReject(StrategyChildOrderAmendReject strategyChildOrderAmendReject) {

    }

    @Override
    public void processStrategyChildOrderCancelAck(StrategyChildOrderCancelAck strategyChildOrderCancelAck) {

    }

    @Override
    public void processStrategyChildOrderCancelReject(StrategyChildOrderCancelReject strategyChildOrderCancelReject) {

    }

    @Override
    public void processStrategyChildOrderExpire(StrategyChildOrderExpire strategyChildOrderExpire) {

    }

    @Override
    public void processStrategyChildOrderUnsolicitedCancel(StrategyChildOrderUnsolicitedCancel strategyChildOrderUnsolicitedCancel) {

    }

    @Override
    public void processStrategyChildOrderFill(StrategyChildOrderFill strategyChildOrderFill) {

    }

    @Override
    public void processStrategyMarketDataEvent(StrategyMarketDataEvent strategyMarketDataEvent) {

    }

    @Override
    public void processStrategyTimerEvent(StrategyTimerEvent strategyTimerEvent) {

    }

    @Override
    public Executor getEventQueue() {
        return null;
    }
}
