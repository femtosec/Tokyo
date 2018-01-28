package jp.co.myogadanimotors.myogadani.eventprocessing;

import java.util.concurrent.Executor;

public interface IAsyncEventListener {
    Executor getEventQueue();
}
