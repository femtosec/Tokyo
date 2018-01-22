package jp.co.myogadanimotors.myogadani.store.masterdata.strategy;

import jp.co.myogadanimotors.myogadani.store.IStoredObject;

public interface IStrategyDescriptor extends IStoredObject {
    String getName();
    String getDescription();
}
