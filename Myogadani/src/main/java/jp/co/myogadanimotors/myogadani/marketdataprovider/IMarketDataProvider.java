package jp.co.myogadanimotors.myogadani.marketdataprovider;

import jp.co.myogadanimotors.myogadani.eventprocessing.marketdata.IAsyncMarketDataListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.marketdata.IAsyncMarketDataRequestListener;

public interface IMarketDataProvider extends IAsyncMarketDataRequestListener {
    void addEventListener(IAsyncMarketDataListener asyncMarketDataListener);
}
