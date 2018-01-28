package jp.co.myogadanimotors.myogadani.marketdataprovider;

import jp.co.myogadanimotors.myogadani.eventprocessing.marketdata.IAsyncMarketDataListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.marketdata.IAsyncMarketDataRequestListener;

public interface IMarketDataProvider extends Runnable, IAsyncMarketDataRequestListener {
    void addEventListener(IAsyncMarketDataListener asyncMarketDataListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
