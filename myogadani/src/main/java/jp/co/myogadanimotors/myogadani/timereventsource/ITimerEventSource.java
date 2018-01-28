package jp.co.myogadanimotors.myogadani.timereventsource;

import jp.co.myogadanimotors.myogadani.event.timer.IAsyncTimerEventListener;
import jp.co.myogadanimotors.myogadani.event.timer.IAsyncTimerRegistrationListener;

public interface ITimerEventSource extends Runnable, IAsyncTimerRegistrationListener {
    void addEventListener(IAsyncTimerEventListener asyncTimerEventListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
