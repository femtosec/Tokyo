package jp.co.myogadanimotors.myogadani.eventprocessing.marketdata;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public interface IAsyncMarketDataListener extends IAsyncEventListener {
    void processMarketData(MarketData marketData);
}
