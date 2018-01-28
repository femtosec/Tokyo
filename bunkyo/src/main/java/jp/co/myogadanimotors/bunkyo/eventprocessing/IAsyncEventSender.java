package jp.co.myogadanimotors.bunkyo.eventprocessing;

public interface IAsyncEventSender<T extends IAsyncEventListener> {
    void addAsyncEventListener(T asyncEventListener);
}
