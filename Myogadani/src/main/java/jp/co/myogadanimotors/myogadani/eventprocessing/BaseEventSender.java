package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.util.ArrayList;
import java.util.List;

import static jp.co.myogadanimotors.myogadani.common.Utility.notNull;

public class BaseEventSender<T extends IAsyncEventListener> implements IAsyncEventSender<T> {

    private final EventIdGenerator idGenerator;
    private final ITimeSource timeSource;
    private final List<T> asyncEventListenerList = new ArrayList<>();

    public BaseEventSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        this.idGenerator = notNull(idGenerator);
        this.timeSource = notNull(timeSource);
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
