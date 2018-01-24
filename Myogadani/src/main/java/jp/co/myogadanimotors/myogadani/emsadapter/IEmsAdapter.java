package jp.co.myogadanimotors.myogadani.emsadapter;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.IAsyncOrderListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncFillListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncReportListener;

public interface IEmsAdapter extends IAsyncReportListener, IAsyncFillListener, IAsyncEventSender {
    void addEventListener(IAsyncOrderListener asyncOrderListener);
}
