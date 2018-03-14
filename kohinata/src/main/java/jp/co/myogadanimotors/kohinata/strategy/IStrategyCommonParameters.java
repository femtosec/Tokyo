package jp.co.myogadanimotors.kohinata.strategy;

public interface IStrategyCommonParameters {
    void init();
    int getMaxNumberOfPendingAmendProcessing();
    int getMaxNumberOfPendingCancelProcessing();
    long getPendingAmendProcessingTimerInterval();
    long getPendingCancelProcessingTimerInterval();
    long getPendingAmendCancelRepetitiveTimerTag();
}
