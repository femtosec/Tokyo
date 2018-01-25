package jp.co.myogadanimotors.myogadani.store.master.market;

import jp.co.myogadanimotors.myogadani.store.IStoredObject;

public interface IMarket extends IStoredObject {
    MarketType getMarketType();
    String getName();
    String getMic();
    MarketState getMarketState();
}
