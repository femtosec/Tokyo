package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.util.ArrayList;
import java.util.List;

public class BaseEventSender<T extends IAsyncEventListener> implements IAsyncEventSender<T> {

    private final EventIdGenerator idGenerator;
    private final ITimeSource timeSource;
    private final List<T> asyncEventListenerList = new ArrayList<>();

    public BaseEventSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        this.idGenerator = idGenerator;
        this.timeSource = timeSource;
    }

    @Override
    public final void addAsyncEventListener(T asyncEventListener) {
        asyncEventListenerList.add(asyncEventListener);
    }

    protected final void send(IEventFactory<T> eventFactory) {
        for (T asyncEventListener : asyncEventListenerList) {
            IEvent event = eventFactory.create(idGenerator.generateEventId(), timeSource.getCurrentTime(), asyncEventListener);
            asyncEventListener.getExecutor().execute(event);
        }
    }
}
