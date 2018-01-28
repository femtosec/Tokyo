package jp.co.myogadanimotors.myogadani.strategy.context;

public interface IStrategyPendingCancelProcessor {
    long getRequestId();
    String getMessage();
    PendingAmendCancelResult getResult();
    void process();
}
