package jp.co.myogadanimotors.myogadani.event.timer;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncTimerEventListener extends IAsyncEventListener {
    void processTimerEvent(TimerEvent timerEvent);
}
