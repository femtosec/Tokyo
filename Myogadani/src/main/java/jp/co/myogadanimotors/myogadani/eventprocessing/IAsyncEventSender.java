package jp.co.myogadanimotors.myogadani.eventprocessing;

public interface IAsyncEventSender<T extends IAsyncEventListener> {
    void addAsyncEventListener(T asyncEventListener);
}
