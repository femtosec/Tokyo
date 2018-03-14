package jp.co.myogadanimotors.kohinata.strategy.event;

public enum StrategyEventType {
    // order
    New,
    NewAck,
    NewReject,
    Amend,
    AmendAck,
    AmendReject,
    Cancel,
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
    ChildOrderExpire,
    ChildOrderUnsolicitedCancel,

    // child order fill
    ChildOrderFill,

    // market data
    MarketData,

    // timer
    Timer
}
