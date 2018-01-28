package jp.co.myogadanimotors.myogadani.eventprocessing;

public interface IEvent extends Runnable {

    /**
     * returns event id
     */
    long getEventId();

    /**
     * returns the time at when this object is created
     */
    long getCreationTime();

    /**
     * returns StringBuilder for outputting contents of this object
     */
    StringBuilder toStringBuilder();
}
