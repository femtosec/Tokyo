package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.strategy.context.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StrategyPendingAmendProcessor implements IStrategyPendingAmendProcessor {

    protected final Logger logger = LogManager.getLogger(getClass().getName());

    private final long requestId;
    private final IChildOrderContainer childOrderContainer;
    private final IChildOrderSender childOrderSender;
    private final int maxNumberOfProcessing;
    private String message;
    private PendingAmendCancelResult result = PendingAmendCancelResult.Working;
    private int numberOfProcessing = 0;

    public StrategyPendingAmendProcessor(long requestId,
                                         IChildOrderContainer childOrderContainer,
                                         IChildOrderSender childOrderSender,
                                         int maxNumberOfProcessing) {
        this.requestId = requestId;
        this.childOrderContainer = childOrderContainer;
        this.childOrderSender = childOrderSender;
        this.maxNumberOfProcessing = maxNumberOfProcessing;
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
        // result check
        if (result != PendingAmendCancelResult.Working) {
            logger.trace("not doing anything since already finished processing. (result: {})", result);
            return;
        }

        // number of processing check
        if (maxNumberOfProcessing == ++numberOfProcessing) {
            message = "finished processing in failure. (numberOfProcessing: " + numberOfProcessing + ")";
            logger.warn(message);
            result = PendingAmendCancelResult.Failed;
            return;
        }

        // on-the-wire check
        if (childOrderContainer.hasOnTheWireChildOrders()) {
            logger.trace("not doing anything since on-thew-wire orders exist.");
            return;
        }

        // exposed quantity check
        if (!childOrderContainer.hasExposedChildOrders()) {
            logger.trace("finished processing in success.");
            result = PendingAmendCancelResult.Succeeded;
            return;
        }

        // cancel exposed child orders
        for (IOrder order : childOrderContainer.getChildOrders()) {
            if (order.getOrderState().isCancellable()) {
                if (!childOrderSender.sendCancelOrder(order.getOrderId())) {
                    logger.info("failed to cancel. (childOrder: {})", order);
                }
            }
        }
    }
}
