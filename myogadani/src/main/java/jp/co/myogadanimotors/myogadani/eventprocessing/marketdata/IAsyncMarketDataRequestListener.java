package jp.co.myogadanimotors.myogadani.eventprocessing.marketdata;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public interface IAsyncMarketDataRequestListener extends IAsyncEventListener {
    void processMarketDataRequest(MarketDataRequest marketDataRequest);
}
