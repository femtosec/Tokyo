package jp.co.myogadanimotors.kohinata.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncFillListener extends IAsyncEventListener {
    void processFillEvent(FillEvent fillEvent);
}
