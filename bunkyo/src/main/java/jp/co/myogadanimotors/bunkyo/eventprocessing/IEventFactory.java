package jp.co.myogadanimotors.bunkyo.eventprocessing;

@FunctionalInterface
public interface IEventFactory<T extends IAsyncEventListener> {
    IEvent create(long eventId, long creationTime, T asyncEventListener);
}
