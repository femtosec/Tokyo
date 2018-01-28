package jp.co.myogadanimotors.kohinata.strategy;

public class StrategyConstants {
    // pending amend/cancel related parameters
    public static final int DEFAULT_MAX_NUMBER_OF_PENDING_AMEND_PROCESSING = 10;
    public static final int DEFAULT_MAX_NUMBER_OF_PENDING_CANCEL_PROCESSING = 10;
    public static final long DEFAULT_PENDING_AMEND_PROCESSING_TIMER_INTERVAL = 100;
    public static final long DEFAULT_PENDING_CANCEL_PROCESSING_TIMER_INTERVAL = 100;
    public static final long PENDING_AMEND_CANCEL_REPETITIVE_TIMER_TAG = 1000000;
}
