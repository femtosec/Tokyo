package jp.co.myogadanimotors.myogadani.strategy.context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrategyPendingAmendProcessor implements IStrategyPendingAmendProcessor {

    protected final Logger logger = LogManager.getLogger(getClass().getName());

    private final long requestId;
    private String message;
    private PendingAmendCancelResult result = PendingAmendCancelResult.Working;

    public StrategyPendingAmendProcessor(long requestId) {
        this.requestId = requestId;
    }

    @Override
    public final long getRequestId() {
        return requestId;
    }

    @Override
    public final String getMessage() {
        return message;
    }

    @Override
    public final PendingAmendCancelResult getResult() {
        return result;
    }

    @Override
    public void process(IStrategyPendingAmendContext pendingAmendContext) {
        logger.trace("processing pending amend state.");
    }
}
