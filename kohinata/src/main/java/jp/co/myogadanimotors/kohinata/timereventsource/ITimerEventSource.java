package jp.co.myogadanimotors.kohinata.timereventsource;

import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerEventListener;
import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerRegistrationListener;

public interface ITimerEventSource extends Runnable, IAsyncTimerRegistrationListener {
    void addEventListener(IAsyncTimerEventListener asyncTimerEventListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
