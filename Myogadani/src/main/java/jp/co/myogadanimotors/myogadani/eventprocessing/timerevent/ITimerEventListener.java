package jp.co.myogadanimotors.myogadani.eventprocessing.timerevent;

import jp.co.myogadanimotors.myogadani.eventprocessing.IEventListener;

public interface ITimerEventListener extends IEventListener {
    void processTimerEvent(TimerEvent timerEvent);
}
