package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.kohinata.strategy.IStrategy;
import jp.co.myogadanimotors.kohinata.strategy.context.IStrategyContext;
import jp.co.myogadanimotors.kohinata.strategy.context.IStrategyPendingAmendContext;
import jp.co.myogadanimotors.kohinata.strategy.context.IStrategyPendingAmendProcessor;
import jp.co.myogadanimotors.kohinata.strategy.context.IStrategyPendingCancelProcessor;
import jp.co.myogadanimotors.kohinata.strategy.validator.IValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStrategy implements IStrategy {

    protected final Logger logger = LogManager.getLogger(getClass().getName());

    private final List<IValidator> validatorList = new ArrayList<>();

    protected final void addValidator(IValidator validator) {
        validatorList.add(validator);
    }

    @Override
    public final IValidator[] getValidators() {
        return validatorList.toArray(new IValidator[validatorList.size()]);
    }

    @Override
    public IStrategyPendingAmendContext createPendingAmendContext(IStrategyContext context) {
        return new StrategyPendingAmendContext();
    }

    @Override
    public IStrategyPendingAmendProcessor createPendingAmendProcessor(IStrategyContext context, long requestId) {
        return new StrategyPendingAmendProcessor(
                requestId,
                context.getChildOrderContainer(),
                context.getChildOrderSender(),
                context.getStrategyParameterAccessor().getInt("maxNumberOfPendingAmendProcessing", StrategyConstants.DEFAULT_MAX_NUMBER_OF_PENDING_AMEND_PROCESSING)
        );
    }

    @Override
    public IStrategyPendingCancelProcessor createPendingCancelProcessor(IStrategyContext context, long requestId) {
        return new StrategyPendingCancelProcessor(
                requestId,
                context.getChildOrderContainer(),
                context.getChildOrderSender(),
                context.getStrategyParameterAccessor().getInt("maxNumberOfPendingCancelProcessing", StrategyConstants.DEFAULT_MAX_NUMBER_OF_PENDING_CANCEL_PROCESSING)
        );
    }
}
