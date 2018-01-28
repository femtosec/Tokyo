package jp.co.myogadanimotors.myogadani.exchangeadapter;

import jp.co.myogadanimotors.myogadani.event.order.IAsyncOrderListener;
import jp.co.myogadanimotors.myogadani.event.report.IAsyncFillListener;
import jp.co.myogadanimotors.myogadani.event.report.IAsyncReportListener;

public interface IExchangeAdapter extends Runnable, IAsyncOrderListener {
    void addEventListeners(IAsyncReportListener asyncReportListener, IAsyncFillListener asyncFillListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
