package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class BaseEventSender<T extends BaseEvent> implements IAsyncEventSender<T, IAsyncEventListener<T>> {

    private final EventIdGenerator idGenerator;
    private final ITimeSource timeSource;
    private final List<EventListenerExecutorPair> eventListenerExecutorPairList = new ArrayList<>();

    public BaseEventSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        this.idGenerator = idGenerator;
        this.timeSource = timeSource;
    }

    protected abstract T createEvent(long eventId, long creationTime, IAsyncEventListener<T> asyncEventListener);

    @Override
    public void addAsyncEventListener(IAsyncEventListener<T> asyncEventListener, Executor executor) {
        eventListenerExecutorPairList.add(new EventListenerExecutorPair(asyncEventListener, executor));
    }

    protected final void send() {
        for (EventListenerExecutorPair eventListenerExecutorPair : eventListenerExecutorPairList) {
            T event = createEvent(idGenerator.generateEventId(), timeSource.getCurrentTime(), eventListenerExecutorPair.asyncEventListener);
            eventListenerExecutorPair.executor.execute(event);
        }
    }

    private class EventListenerExecutorPair {
        private final IAsyncEventListener<T> asyncEventListener;
        private final Executor executor;

        private EventListenerExecutorPair(IAsyncEventListener<T> asyncEventListener, Executor executor) {
            this.asyncEventListener = asyncEventListener;
            this.executor = executor;
        }
    }
}
