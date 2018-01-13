package jp.co.myogadanimotors.myogadani.eventprocessing.timerregistration;

import jp.co.myogadanimotors.myogadani.eventprocessing.IEventListener;

public interface ITimerRegistrationListener extends IEventListener {
    void processTimerRegistration(TimerRegistration timerRegistration);
}
