package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.FillSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.ReportSender;
import jp.co.myogadanimotors.myogadani.idgenerator.IIdGenerator;
import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.IProduct;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.strategy.StrategyMaster;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public abstract class AbstractStrategyFactory implements IStrategyFactory {

    private final ReportSender reportSender;
    private final FillSender fillSender;
    private final EventIdGenerator eventIdGenerator;
    private final ITimeSource timeSource;
    private final IIdGenerator requestIdGenerator;
    private final MarketMaster marketMaster;
    private final ProductMaster productMaster;
    private final StrategyMaster strategyMaster;

    public AbstractStrategyFactory(ReportSender reportSender,
                                   FillSender fillSender,
                                   EventIdGenerator eventIdGenerator,
                                   ITimeSource timeSource,
                                   IIdGenerator requestIdGenerator,
                                   MarketMaster marketMaster,
                                   ProductMaster productMaster,
                                   StrategyMaster strategyMaster) {
        this.reportSender = reportSender;
        this.fillSender = fillSender;
        this.eventIdGenerator = eventIdGenerator;
        this.timeSource = timeSource;
        this.requestIdGenerator = requestIdGenerator;
        this.marketMaster = marketMaster;
        this.productMaster = productMaster;
        this.strategyMaster = strategyMaster;
    }

    protected final StrategyContext createStrategyContext(IOrder order) {
        IMarket market = marketMaster.getByMic(order.getMic());
        IProduct product = productMaster.getBySymbol(order.getSymbol());
        return new StrategyContext(
                reportSender,
                fillSender,
                eventIdGenerator,
                timeSource,
                requestIdGenerator,
                strategyMaster,
                order,
                market,
                product
        );
    }
}
