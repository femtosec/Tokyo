package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.bunkyo.master.market.IMarket;
import jp.co.myogadanimotors.bunkyo.master.product.IProduct;
import jp.co.myogadanimotors.kohinata.master.strategy.IStrategyDescriptor;
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder;
import jp.co.myogadanimotors.kohinata.strategy.StrategyState;
import jp.co.myogadanimotors.kohinata.strategy.event.AbstractStrategyEvent;

public interface IStrategyContext {
    StrategyState getStrategyState();
    IOrder getOrder();
    IMarket getMarket();
    IProduct getProduct();
    IStrategyDescriptor getStrategyDescriptor();
    IStrategyParameterAccessor getStrategyParameterAccessor();
    IChildOrderContainer getChildOrderContainer();
    IChildOrderSender getChildOrderSender();
    ITimerRegistry getTimerRegistry();
    AbstractStrategyEvent getLastStrategyEvent();
    long getCurrentTime();
    void subscribeMarketData(String symbol, String mic);
}
