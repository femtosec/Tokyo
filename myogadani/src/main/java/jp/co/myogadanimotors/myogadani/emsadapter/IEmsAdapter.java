package jp.co.myogadanimotors.myogadani.emsadapter;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventSender;
import jp.co.myogadanimotors.myogadani.event.order.IAsyncOrderListener;
import jp.co.myogadanimotors.myogadani.event.report.IAsyncFillListener;
import jp.co.myogadanimotors.myogadani.event.report.IAsyncReportListener;

public interface IEmsAdapter extends Runnable, IAsyncReportListener, IAsyncFillListener, IAsyncEventSender {
    void addEventListener(IAsyncOrderListener asyncOrderListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
