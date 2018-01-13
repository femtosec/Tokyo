package jp.co.myogadanimotors.myogadani.eventprocessing.fill;

import jp.co.myogadanimotors.myogadani.eventprocessing.IEventListener;

public interface IFillEventListener extends IEventListener {
    void processFillEvent(FillEvent fillEvent);
}
