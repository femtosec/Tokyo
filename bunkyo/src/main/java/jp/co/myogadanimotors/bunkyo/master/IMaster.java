package jp.co.myogadanimotors.bunkyo.master;

import java.util.function.Consumer;

public interface IMaster<T extends IStoredObject> {
    T get(long id);
    void forEach(Consumer<T> visitor);
}
