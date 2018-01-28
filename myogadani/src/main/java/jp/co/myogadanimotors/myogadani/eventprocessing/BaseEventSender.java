package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.requireNonNull;

public class BaseEventSender<T extends IAsyncEventListener> implements IAsyncEventSender<T> {

    private final EventIdGenerator idGenerator;
    private final ITimeSource timeSource;
    private final List<T> asyncEventListenerList = new CopyOnWriteArrayList<>();

    public BaseEventSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        this.idGenerator = requireNonNull(idGenerator);
        this.timeSource = requireNonNull(timeSource);
    }

    @Override
    public final void addAsyncEventListener(T asyncEventListener) {
        asyncEventListenerList.add(requireNonNull(asyncEventListener));
    }

    protected final void send(IEventFactory<T> eventFactory) {
        for (T asyncEventListener : asyncEventListenerList) {
            IEvent event = eventFactory.create(idGenerator.generateEventId(), timeSource.getCurrentTime(), asyncEventListener);
            asyncEventListener.getEventQueue().execute(event);
        }
    }
}
