package jp.co.myogadanimotors.myogadani.store.master.strategy;

import jp.co.myogadanimotors.myogadani.store.IStoredObject;

public interface IStrategyDescriptor extends IStoredObject {
    String getName();
    String getDescription();
}
