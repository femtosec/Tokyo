package jp.co.myogadanimotors.myogadani.eventprocessing;

public interface IEvent {
    /**
     * returns event id
     */
    long getEventId();

    /**
     * returns event type
     */
    EventType getEventType();

    /**
     * returns the event sender name which created this object
     */
    String getEventSenderName();

    /**
     * returns the time at when this object is created
     */
    long getCreationTime();

    /**
     * returns StringBuilder for outputting contents of this object
     */
    StringBuilder toStringBuilder();
}
