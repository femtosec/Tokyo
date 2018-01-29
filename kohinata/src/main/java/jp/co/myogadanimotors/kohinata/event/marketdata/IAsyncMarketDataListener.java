package jp.co.myogadanimotors.kohinata.event.marketdata;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncMarketDataListener extends IAsyncEventListener {
    void processMarketData(MarketData marketData);
}
