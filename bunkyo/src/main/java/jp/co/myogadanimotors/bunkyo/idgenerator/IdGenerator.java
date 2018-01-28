package jp.co.myogadanimotors.bunkyo.idgenerator;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private final AtomicLong idSource;

    public IdGenerator() {
        this(0L);
    }

    public IdGenerator(long initialId) {
        idSource = new AtomicLong(initialId);
    }

    public long generateId() {
        return idSource.getAndIncrement();
    }
}
