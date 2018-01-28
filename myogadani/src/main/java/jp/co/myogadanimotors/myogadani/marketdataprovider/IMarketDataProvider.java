package jp.co.myogadanimotors.myogadani.marketdataprovider;

import jp.co.myogadanimotors.myogadani.event.marketdata.IAsyncMarketDataListener;
import jp.co.myogadanimotors.myogadani.event.marketdata.IAsyncMarketDataRequestListener;

public interface IMarketDataProvider extends Runnable, IAsyncMarketDataRequestListener {
    void addEventListener(IAsyncMarketDataListener asyncMarketDataListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
