package jp.co.myogadanimotors.myogadani.strategy;

public interface IStrategyParameters {
    void init();
    int getMaxNumberOfPendingAmendProcessing();
    int getMaxNumberOfPendingCancelProcessing();
    long getPendingAmendProcessingTimerInterval();
    long getPendingCancelProcessingTimerInterval();
    long getPendingAmendCancelRepetitiveTimerTag();
}
