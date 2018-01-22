package jp.co.myogadanimotors.myogadani.strategy;

import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketMaster;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.IProduct;
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductMaster;
import jp.co.myogadanimotors.myogadani.strategy.context.StrategyContext;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public abstract class AbstractStrategyFactory implements IStrategyFactory {

    private final ITimeSource timeSource;
    private final MarketMaster marketMaster;
    private final ProductMaster productMaster;

    public AbstractStrategyFactory(ITimeSource timeSource, MarketMaster marketMaster, ProductMaster productMaster) {
        this.timeSource = timeSource;
        this.marketMaster = marketMaster;
        this.productMaster = productMaster;
    }

    protected final StrategyContext createStrategyContext(IOrder order) {
        IMarket market = marketMaster.getByMic(order.getMic());
        IProduct product = productMaster.getBySymbol(order.getSymbol());
        return new StrategyContext(timeSource, order, market, product);
    }
}
