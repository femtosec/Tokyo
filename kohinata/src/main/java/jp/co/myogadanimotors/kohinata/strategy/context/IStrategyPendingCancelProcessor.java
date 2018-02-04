package jp.co.myogadanimotors.kohinata.strategy.context;

public interface IStrategyPendingCancelProcessor {
    long getRequestId();
    String getMessage();
    PendingAmendCancelResult getResult();
    void process();
}
