package jp.co.myogadanimotors.myogadani.common;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Constants {
    public static final int NOT_SET_ID_INT = -1;
    public static final long NOT_SET_ID_LONG = -1;
    public static final String CONFIG_FILE_NAME = "configuration.groovy";
    public static final int DEFAULT_NUMBER_OF_STRATEGY_THREADS = 1;

    // pending amend/cancel related parameters
    public static final int DEFAULT_MAX_NUMBER_OF_PENDING_AMEND_PROCESSING = 10;
    public static final int DEFAULT_MAX_NUMBER_OF_PENDING_CANCEL_PROCESSING = 10;
    public static final long DEFAULT_PENDING_AMEND_PROCESSING_TIMER_INTERVAL = MILLISECONDS.toNanos(100);
    public static final long DEFAULT_PENDING_CANCEL_PROCESSING_TIMER_INTERVAL = MILLISECONDS.toNanos(100);
    public static final long PENDING_AMEND_CANCEL_REPETITIVE_TIMER_TAG = 1000000;
}
