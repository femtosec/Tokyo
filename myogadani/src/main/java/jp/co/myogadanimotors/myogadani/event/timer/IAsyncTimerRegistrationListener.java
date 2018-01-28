package jp.co.myogadanimotors.myogadani.event.timer;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncTimerRegistrationListener extends IAsyncEventListener {
    void processTimerRegistration(TimerRegistration timerRegistration);
}
