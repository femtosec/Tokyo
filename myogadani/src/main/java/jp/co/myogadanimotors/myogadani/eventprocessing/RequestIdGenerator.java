package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.idgenerator.IIdGenerator;
import jp.co.myogadanimotors.myogadani.idgenerator.IdGenerator;

public final class RequestIdGenerator {

    private final IIdGenerator idGenerator;

    public RequestIdGenerator() {
        this(0L);
    }

    public RequestIdGenerator(long initialId) {
        idGenerator = new IdGenerator(initialId);
    }

    public long generateRequestId() {
        return idGenerator.generateId();
    }
}
