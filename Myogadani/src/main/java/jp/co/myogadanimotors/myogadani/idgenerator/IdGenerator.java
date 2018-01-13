package jp.co.myogadanimotors.myogadani.idgenerator;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator implements IIdGenerator {

    private final AtomicLong idSource;

    public IdGenerator() {
        this(0L);
    }

    public IdGenerator(long initialId) {
        idSource = new AtomicLong(initialId);
    }


    @Override
    public long generateId() {
        return idSource.getAndIncrement();
    }
}
