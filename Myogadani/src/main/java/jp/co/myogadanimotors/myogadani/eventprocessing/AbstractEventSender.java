package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractEventSender<T extends AbstractEvent> implements IEventSender {

    private final Logger logger;

    private final String eventSenderName;
    private final IEventIdGenerator idGenerator;
    private final ITimeSource timeSource;
    private final IEventStream[] eventStreams;

    public AbstractEventSender(String eventSenderName, IEventIdGenerator idGenerator, ITimeSource timeSource, IEventStream... eventStreams) {
        logger = LogManager.getLogger(getClass().getName() + "-" + eventSenderName);
        this.eventSenderName = eventSenderName;
        this.idGenerator = idGenerator;
        this.timeSource = timeSource;
        this.eventStreams = eventStreams;
    }

    protected final void send() {
        T event = createEvent();
        for (IEventStream eventStream : eventStreams) {
            eventStream.put(event);
        }
    }

    protected final String getEventSenderName() {
        return eventSenderName;
    }

    protected final long generateEventId() {
        return idGenerator.generateEventId();
    }

    protected final long getCurrentTime() {
        return timeSource.getCurrentTime();
    }

    protected abstract T createEvent();
}
