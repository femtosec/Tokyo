package jp.co.myogadanimotors.myogadani.eventprocessing;

@FunctionalInterface
public interface IAsyncEventListener<T extends IEvent> {
    void handleEvent(T event);
}
