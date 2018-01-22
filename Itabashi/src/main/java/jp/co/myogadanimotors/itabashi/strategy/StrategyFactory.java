package jp.co.myogadanimotors.itabashi.strategy;

import jp.co.myogadanimotors.itabashi.strategy.peg.Peg;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.FillSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.ReportSender;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.strategy.AbstractStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.IStrategy;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class StrategyFactory extends AbstractStrategyFactory {

    public StrategyFactory(ReportSender reportSender,
                           FillSender fillSender,
                           ITimeSource timeSource,
                           MarketMaster marketMaster,
                           ProductMaster productMaster) {
        super(reportSender, fillSender, timeSource, marketMaster, productMaster);
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
