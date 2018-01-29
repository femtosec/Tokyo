package jp.co.myogadanimotors.kohinata.event.timer;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncTimerEventListener extends IAsyncEventListener {
    void processTimerEvent(TimerEvent timerEvent);
}
