package jp.co.myogadanimotors.kohinata.event.marketdata;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncMarketDataRequestListener extends IAsyncEventListener {
    void processMarketDataRequest(MarketDataRequest marketDataRequest);
}
