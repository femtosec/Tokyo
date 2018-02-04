package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.bunkyo.master.market.IMarket;
import jp.co.myogadanimotors.bunkyo.master.product.IProduct;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.strategy.StrategyState;

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
