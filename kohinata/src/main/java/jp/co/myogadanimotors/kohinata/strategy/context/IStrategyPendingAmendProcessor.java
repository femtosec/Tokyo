package jp.co.myogadanimotors.kohinata.strategy.context;

public interface IStrategyPendingAmendProcessor {
    long getRequestId();
    String getMessage();
    PendingAmendCancelResult getResult();
    void process(IStrategyPendingAmendContext pendingAmendContext);
}
