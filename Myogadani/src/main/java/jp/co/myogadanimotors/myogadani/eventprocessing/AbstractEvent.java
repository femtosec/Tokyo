package jp.co.myogadanimotors.myogadani.eventprocessing;

public abstract class AbstractEvent implements IEvent {

    private final long eventId;
    private final long creationTime;
    private final String eventSenderName;

    public AbstractEvent(long eventId, long creationTime, String eventSenderName) {
        this.eventId = eventId;
        this.creationTime = creationTime;
        this.eventSenderName = eventSenderName;
    }

    @Override
    public final long getEventId() {
        return eventId;
    }

    @Override
    public String getEventSenderName() {
        return eventSenderName;
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
        return new StringBuilder().append(getClass().getName())
                .append(" eventId: ").append(eventId)
                .append(", eventType: ").append(getEventType())
                .append(", eventSenderName: ").append(eventSenderName)
                .append(", creationTime: ").append(creationTime);
    }
}
