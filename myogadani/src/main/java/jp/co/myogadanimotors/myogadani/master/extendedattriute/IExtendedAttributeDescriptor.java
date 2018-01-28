package jp.co.myogadanimotors.myogadani.master.extendedattriute;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;

public interface IExtendedAttributeDescriptor extends IStoredObject {
    String getName();
    String getType();
    String getDescription();
}
