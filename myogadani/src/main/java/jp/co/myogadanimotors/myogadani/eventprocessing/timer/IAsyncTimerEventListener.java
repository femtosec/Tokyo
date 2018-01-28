package jp.co.myogadanimotors.myogadani.eventprocessing.timer;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public interface IAsyncTimerEventListener extends IAsyncEventListener {
    void processTimerEvent(TimerEvent timerEvent);
}
