package jp.co.myogadanimotors.myogadani.event.marketdata;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncMarketDataListener extends IAsyncEventListener {
    void processMarketData(MarketData marketData);
}
