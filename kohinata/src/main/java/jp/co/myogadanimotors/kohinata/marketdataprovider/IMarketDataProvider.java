package jp.co.myogadanimotors.kohinata.marketdataprovider;

import jp.co.myogadanimotors.kohinata.event.marketdata.IAsyncMarketDataListener;
import jp.co.myogadanimotors.kohinata.event.marketdata.IAsyncMarketDataRequestListener;

public interface IMarketDataProvider extends Runnable, IAsyncMarketDataRequestListener {
    void addEventListener(IAsyncMarketDataListener asyncMarketDataListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
