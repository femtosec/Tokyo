package jp.co.myogadanimotors.kohinata.exchangeadapter;

import jp.co.myogadanimotors.kohinata.event.order.IAsyncOrderListener;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncFillListener;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncReportListener;

public interface IExchangeAdapter extends Runnable, IAsyncOrderListener {
    void addEventListeners(IAsyncReportListener asyncReportListener, IAsyncFillListener asyncFillListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
