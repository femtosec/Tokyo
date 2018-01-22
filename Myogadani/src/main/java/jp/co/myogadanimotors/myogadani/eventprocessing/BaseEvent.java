package jp.co.myogadanimotors.myogadani.eventprocessing;

public abstract class BaseEvent<T extends IAsyncEventListener> implements IEvent {

    private final long eventId;
    private final long creationTime;
    private final T eventListener;

    public BaseEvent(long eventId, long creationTime, T eventListener) {
        this.eventId = eventId;
        this.creationTime = creationTime;
        this.eventListener = notNull(eventListener);
    }

    protected abstract void callEventListener(T eventListener);

    @Override
    public final void run() {
        callEventListener(eventListener);
    }

    @Override
    public final long getEventId() {
        return eventId;
    }

    @Override
    public final long getCreationTime() {
        return creationTime;
    }

    @Override
    public final String toString() {
        return toStringBuilder().toString();
    }

    @Override
    public StringBuilder toStringBuilder() {
        return new StringBuilder(getClass().getName())
                .append(" eventId: ").append(eventId)
                .append(", creationTime: ").append(creationTime);
    }

    protected static <U> U notNull(U value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }
}
