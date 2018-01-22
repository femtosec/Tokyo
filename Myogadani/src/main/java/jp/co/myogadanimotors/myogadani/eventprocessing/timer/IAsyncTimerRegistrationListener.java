package jp.co.myogadanimotors.myogadani.eventprocessing.timer;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public interface IAsyncTimerRegistrationListener extends IAsyncEventListener {
    void processTimerRegistration(TimerRegistration timerRegistration);
}
