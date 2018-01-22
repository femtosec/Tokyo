package jp.co.myogadanimotors.itabashi.strategy;

import jp.co.myogadanimotors.itabashi.strategy.peg.Peg;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.FillSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.ReportSender;
import jp.co.myogadanimotors.myogadani.idgenerator.IIdGenerator;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.StrategyMaster;
import jp.co.myogadanimotors.myogadani.strategy.AbstractStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class StrategyFactory extends AbstractStrategyFactory {

    public StrategyFactory(ReportSender reportSender,
                           FillSender fillSender,
                           EventIdGenerator eventIdGenerator,
                           ITimeSource timeSource,
                           IIdGenerator requestIdGenerator,
                           MarketMaster marketMaster,
                           ProductMaster productMaster,
                           StrategyMaster strategyMaster) {
        super(
                reportSender,
                fillSender,
                eventIdGenerator,
                timeSource,
                requestIdGenerator,
                marketMaster,
                productMaster,
                strategyMaster
        );
    }

    @Override
    public IStrategy create(int strategyTypeId, IOrder order) {
        StrategyContext context = createStrategyContext(order);

        // todo: to be implemented
        switch (strategyTypeId) {
            case 0:     // test
                return new Peg(strategyTypeId, context);
        }

        return null;
    }
}
