package jp.co.myogadanimotors.kohinata.master.market;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;

public interface IMarket extends IStoredObject {
    MarketType getMarketType();
    String getName();
    String getMic();
    MarketState getMarketState();
}
