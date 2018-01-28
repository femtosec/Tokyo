package jp.co.myogadanimotors.myogadani.master.strategy;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;

public interface IStrategyDescriptor extends IStoredObject {
    String getName();
    String getDescription();
}
