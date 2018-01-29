package jp.co.myogadanimotors.kohinata.master.strategy;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;

public interface IStrategyDescriptor extends IStoredObject {
    String getName();
    String getDescription();
}
