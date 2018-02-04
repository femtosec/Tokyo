package jp.co.myogadanimotors.myogadani.strategy.parameter;

import jp.co.myogadanimotors.bunkyo.config.IConfigAccessor;
import jp.co.myogadanimotors.kohinata.strategy.IStrategyParameters;
import jp.co.myogadanimotors.myogadani.strategy.StrategyConstants;

import static java.util.Objects.requireNonNull;

public class BaseStrategyParameters implements IStrategyParameters {

    private final String strategyName;
    private final IConfigAccessor strategyConfigAccessor;

    private int maxNumberOfPendingAmendProcessing = Integer.MIN_VALUE;
    private int maxNumberOfPendingCancelProcessing = Integer.MIN_VALUE;
    private long pendingAmendProcessingTimerInterval = Long.MIN_VALUE;
    private long pendingCancelProcessingTimerInterval = Long.MIN_VALUE;

    public BaseStrategyParameters(String strategyName, IConfigAccessor strategyConfigAccessor) {
        this.strategyName = requireNonNull(strategyName);
        this.strategyConfigAccessor = requireNonNull(strategyConfigAccessor);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // initializer
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init() {
        maxNumberOfPendingAmendProcessing = getConfigInInt("maxNumberOfPendingAmendProcessing", StrategyConstants.DEFAULT_MAX_NUMBER_OF_PENDING_AMEND_PROCESSING);
        maxNumberOfPendingCancelProcessing = getConfigInInt("maxNumberOfPendingCancelProcessing", StrategyConstants.DEFAULT_MAX_NUMBER_OF_PENDING_CANCEL_PROCESSING);
        pendingAmendProcessingTimerInterval = getConfigInLong("pendingAmendProcessingTimerInterval", StrategyConstants.DEFAULT_PENDING_AMEND_PROCESSING_TIMER_INTERVAL);
        pendingCancelProcessingTimerInterval = getConfigInLong("pendingCancelProcessingTimerInterval", StrategyConstants.DEFAULT_PENDING_CANCEL_PROCESSING_TIMER_INTERVAL);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // getters
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int getMaxNumberOfPendingAmendProcessing() {
        return maxNumberOfPendingAmendProcessing;
    }

    @Override
    public int getMaxNumberOfPendingCancelProcessing() {
        return maxNumberOfPendingCancelProcessing;
    }

    @Override
    public long getPendingAmendProcessingTimerInterval() {
        return pendingAmendProcessingTimerInterval;
    }

    @Override
    public long getPendingCancelProcessingTimerInterval() {
        return pendingCancelProcessingTimerInterval;
    }

    @Override
    public long getPendingAmendCancelRepetitiveTimerTag() {
        return StrategyConstants.PENDING_AMEND_CANCEL_REPETITIVE_TIMER_TAG;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // utilities
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    protected int getConfigInInt(String parameterName, int defaultValue) {
        return strategyConfigAccessor.getInt(
                getStrategySpecificKey(parameterName),
                strategyConfigAccessor.getInt(getDefaultKey(parameterName), defaultValue)
        );
    }

    protected long getConfigInLong(String parameterName, long defaultValue) {
        return strategyConfigAccessor.getLong(
                getStrategySpecificKey(parameterName),
                strategyConfigAccessor.getLong(getDefaultKey(parameterName), defaultValue)
        );
    }

    protected double getConfigInDouble(String parameterName, double defaultValue) {
        return strategyConfigAccessor.getDouble(
                getStrategySpecificKey(parameterName),
                strategyConfigAccessor.getDouble(getDefaultKey(parameterName), defaultValue)
        );
    }

    protected boolean getConfigInBoolean(String parameterName, boolean defaultValue) {
        return strategyConfigAccessor.getBoolean(
                getStrategySpecificKey(parameterName),
                strategyConfigAccessor.getBoolean(getDefaultKey(parameterName), defaultValue)
        );
    }

    protected String getConfigInString(String parameterName, String defaultValue) {
        return strategyConfigAccessor.getString(
                getStrategySpecificKey(parameterName),
                strategyConfigAccessor.getString(getDefaultKey(parameterName), defaultValue)
        );
    }

    private String getDefaultKey(String parameterName) {
        return "strategy.*." + parameterName;
    }

    private String getStrategySpecificKey(String parameterName) {
        return "strategy." + strategyName + "." + parameterName;
    }
}
