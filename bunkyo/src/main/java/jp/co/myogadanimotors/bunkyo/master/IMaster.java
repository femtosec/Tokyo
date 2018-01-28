package jp.co.myogadanimotors.bunkyo.master;

public interface IMaster<T extends IStoredObject> {
    T get(long id);
}
