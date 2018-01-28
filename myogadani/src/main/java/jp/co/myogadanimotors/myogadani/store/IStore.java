package jp.co.myogadanimotors.myogadani.store;

public interface IStore<T extends IStoredObject> {

    T get(long id);
}
