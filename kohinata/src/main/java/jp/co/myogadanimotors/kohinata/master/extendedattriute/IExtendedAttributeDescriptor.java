package jp.co.myogadanimotors.kohinata.master.extendedattriute;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;

public interface IExtendedAttributeDescriptor extends IStoredObject {
    String getName();
    String getType();
    String getDescription();
}
