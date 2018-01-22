package jp.co.myogadanimotors.myogadani.eventprocessing.report;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public interface IAsyncFillListener extends IAsyncEventListener {
    void processFillEvent(FillEvent fillEvent);
}
