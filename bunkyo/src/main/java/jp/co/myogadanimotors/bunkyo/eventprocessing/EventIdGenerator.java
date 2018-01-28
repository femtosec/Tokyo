package jp.co.myogadanimotors.bunkyo.eventprocessing;

import jp.co.myogadanimotors.bunkyo.idgenerator.IdGenerator;

public final class EventIdGenerator {

    private final IdGenerator idGenerator;

    public EventIdGenerator() {
        this(0L);
    }

    public EventIdGenerator(long initialId) {
        idGenerator = new IdGenerator(initialId);
    }

    public long generateEventId() {
        return idGenerator.generateId();
    }
}
