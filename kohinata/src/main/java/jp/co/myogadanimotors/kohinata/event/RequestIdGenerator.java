package jp.co.myogadanimotors.kohinata.event;

import jp.co.myogadanimotors.bunkyo.idgenerator.IdGenerator;

public final class RequestIdGenerator {

    private final IdGenerator idGenerator;

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
