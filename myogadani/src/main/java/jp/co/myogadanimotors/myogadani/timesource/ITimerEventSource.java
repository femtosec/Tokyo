package jp.co.myogadanimotors.myogadani.timesource;

import jp.co.myogadanimotors.myogadani.eventprocessing.timer.IAsyncTimerEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timer.IAsyncTimerRegistrationListener;

public interface ITimerEventSource extends Runnable, IAsyncTimerRegistrationListener {
    void addEventListener(IAsyncTimerEventListener asyncTimerEventListener);
    void shutdown();
    boolean awaitTermination(long timeOut) throws InterruptedException;
}
