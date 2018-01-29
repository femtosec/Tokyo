package jp.co.myogadanimotors.kohinata.event.timer;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncTimerRegistrationListener extends IAsyncEventListener {
    void processTimerRegistration(TimerRegistration timerRegistration);
}
