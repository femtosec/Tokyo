package jp.co.myogadanimotors.kohinata.emsadapter;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventSender;
import jp.co.myogadanimotors.kohinata.event.order.IAsyncOrderListener;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncFillListener;
import jp.co.myogadanimotors.kohinata.event.report.IAsyncReportListener;

public interface IEmsAdapter extends Runnable, IAsyncReportListener, IAsyncFillListener, IAsyncEventSender {
    void addEventListener(IAsyncOrderListener asyncOrderListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
