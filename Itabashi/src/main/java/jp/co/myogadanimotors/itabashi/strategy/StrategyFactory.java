package jp.co.myogadanimotors.itabashi.strategy;

import jp.co.myogadanimotors.itabashi.strategy.peg.Peg;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.RequestIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.marketdata.IAsyncMarketDataRequestListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.IAsyncOrderListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncFillListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncReportListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.IAsyncTimerRegistrationListener;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.StrategyMaster;
import jp.co.myogadanimotors.myogadani.strategy.AbstractStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class StrategyFactory extends AbstractStrategyFactory {

    public StrategyFactory(EventIdGenerator eventIdGenerator,
                           RequestIdGenerator requestIdGenerator,
                           ITimeSource timeSource,
                           MarketMaster marketMaster,
                           ProductMaster productMaster,
                           StrategyMaster strategyMaster,
                           IAsyncOrderListener asyncOrderListener,
                           IAsyncReportListener asyncReportListener,
                           IAsyncFillListener asyncFillListener,
                           IAsyncMarketDataRequestListener asyncMarketDataRequestListener,
                           IAsyncTimerRegistrationListener asyncTimerRegistrationListener) {
        super(
                eventIdGenerator,
                requestIdGenerator,
                timeSource,
                marketMaster,
                productMaster,
                strategyMaster,
                asyncOrderListener,
                asyncReportListener,
                asyncFillListener,
                asyncMarketDataRequestListener,
                asyncTimerRegistrationListener
        );
    }

    @Override
    public IStrategy create(String strategyName, IOrder order) {
        IStrategyDescriptor strategyDescriptor = getStrategyDescriptor(strategyName);
        StrategyContext context = createStrategyContext(order);

        // todo: to be implemented
        switch (strategyName) {
            case "Peg":     // test
                return new Peg(strategyDescriptor, context);
        }

        return null;
    }
}
