package jp.co.myogadanimotors.bunkyo.eventprocessing;

import java.util.concurrent.Executor;

public interface IAsyncEventListener {
    Executor getEventQueue();
}
