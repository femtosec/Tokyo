package jp.co.myogadanimotors.myogadani.strategy.strategyparameter;

public interface IStrategyParameters {
    void init();
    int getMaxNumberOfPendingAmendProcessing();
    int getMaxNumberOfPendingCancelProcessing();
    long getPendingAmendProcessingTimerInterval();
    long getPendingCancelProcessingTimerInterval();
}
