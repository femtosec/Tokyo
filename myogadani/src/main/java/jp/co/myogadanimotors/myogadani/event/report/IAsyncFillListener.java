package jp.co.myogadanimotors.myogadani.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncFillListener extends IAsyncEventListener {
    void processFillEvent(FillEvent fillEvent);
}
