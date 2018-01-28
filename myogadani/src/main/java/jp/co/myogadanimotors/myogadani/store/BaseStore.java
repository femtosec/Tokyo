package jp.co.myogadanimotors.myogadani.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseStore<T extends IStoredObject> implements IStore<T> {

    protected final Map<Long, T> objectsById = new ConcurrentHashMap<>();

    @Override
    public T get(long id) {
        return objectsById.get(id);
    }

    protected void put(T object) {
        objectsById.put(object.getId(), object);
    }
}
