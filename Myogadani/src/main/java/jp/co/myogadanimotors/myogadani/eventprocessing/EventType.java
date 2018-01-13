package jp.co.myogadanimotors.myogadani.eventprocessing;

public enum EventType {
    Order,
    Report,
    Fill,
    MarketDataRequest,
    MarketData,
    TimerRegistration,
    TimerEvent,
    FieldUpdate,
    StrategyInvocation,
    EventStreamTermination
}
