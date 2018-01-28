package jp.co.myogadanimotors.myogadani.eventprocessing;

@FunctionalInterface
public interface IEventFactory<T extends IAsyncEventListener> {
    IEvent create(long eventId, long creationTime, T asyncEventListener);
}
