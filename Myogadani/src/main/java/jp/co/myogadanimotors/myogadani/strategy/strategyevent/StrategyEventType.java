package jp.co.myogadanimotors.myogadani.strategy.strategyevent;

public enum StrategyEventType {
    // parent order
    New,
    Amend,
    Cancel,
    NewAck,
    NewReject,
    AmendAck,
    AmendReject,
    CancelAck,
    CancelReject,
    UnsolicitedCancel,

    // child order
    ChildOrderNewAck,
    ChildOrderNewReject,
    ChildOrderAmendAck,
    ChildOrderAmendReject,
    ChildOrderCancelAck,
    ChildOrderCancelReject,
    ChildOrderFill,
    ChildOrderExpire,
    ChildOrderUnsolicitedCancel,

    // market data event
    MarketData,

    // timer event
    Timer,
}
