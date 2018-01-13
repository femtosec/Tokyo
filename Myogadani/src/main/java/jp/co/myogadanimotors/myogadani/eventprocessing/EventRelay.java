package jp.co.myogadanimotors.myogadani.eventprocessing;

public class EventRelay implements IEventRelay {

    private final IEventStream[] eventStreams;

    public EventRelay(IEventStream... eventStreams) {
        this.eventStreams = eventStreams;
    }

    @Override
    public void relay(IEvent event) {
        for (IEventStream eventStream : eventStreams) {
            eventStream.put(event);
        }
    }
}
