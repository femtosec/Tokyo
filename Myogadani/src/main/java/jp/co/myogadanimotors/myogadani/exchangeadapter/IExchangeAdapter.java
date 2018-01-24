package jp.co.myogadanimotors.myogadani.exchangeadapter;

import jp.co.myogadanimotors.myogadani.eventprocessing.order.IAsyncOrderListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncFillListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IAsyncReportListener;

public interface IExchangeAdapter extends IAsyncOrderListener {
    void addReportListener(IAsyncReportListener asyncReportListener);
    void addFillListener(IAsyncFillListener asyncFillListener);
}
