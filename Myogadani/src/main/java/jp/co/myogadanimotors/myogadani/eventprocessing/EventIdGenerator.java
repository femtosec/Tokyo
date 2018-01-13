package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.idgenerator.IIdGenerator;
import jp.co.myogadanimotors.myogadani.idgenerator.IdGenerator;

public class EventIdGenerator implements IEventIdGenerator {

    private final IIdGenerator idGenerator;

    public EventIdGenerator() {
        this(0L);
    }

    public EventIdGenerator(long initialId) {
        idGenerator = new IdGenerator(initialId);
    }

    @Override
    public long generateEventId() {
        return idGenerator.generateId();
    }
}
