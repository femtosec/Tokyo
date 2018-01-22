package jp.co.myogadanimotors.myogadani.eventprocessing;

import java.util.concurrent.Executor;

public interface IAsyncEventSender<T extends IEvent, U extends IAsyncEventListener<T>> {
    void addAsyncEventListener(IAsyncEventListener<T> asyncEventListener, Executor executor);
}
