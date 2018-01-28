package jp.co.myogadanimotors.myogadani.store.master.extendedattriute;

import jp.co.myogadanimotors.myogadani.store.IStoredObject;

public interface IExtendedAttributeDescriptor extends IStoredObject {
    String getName();
    String getType();
    String getDescription();
}
