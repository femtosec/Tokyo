package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.myogadani.store.master.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.master.product.IProduct;
import jp.co.myogadanimotors.myogadani.strategy.StrategyState;

public interface IStrategyContext {
    StrategyState getStrategyState();
    IOrder getOrder();
    IMarket getMarket();
    IProduct getProduct();
    IChildOrderContainer getChildOrderContainer();
    IChildOrderSender getChildOrderSender();
    ITimerRegistry getTimerRegistry();
    long getCurrentTime();
    void subscribeMarketData(String symbol, String mic);
}
