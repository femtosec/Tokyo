package jp.co.myogadanimotors.myogadani.store.master.extendedattriute;

import jp.co.myogadanimotors.myogadani.store.master.AbstractDataMaster;

public final class ExtendedAttributeMaster extends AbstractDataMaster<IExtendedAttributeDescriptor> {

    @Override
    protected IExtendedAttributeDescriptor create() {
        return new ExtendedAttributeDescriptor();
    }

    public IExtendedAttributeDescriptor getByName(String name) {
        for (IExtendedAttributeDescriptor ead : objectsById.values()) {
            if (ead.getName().equals(name)) return ead;
        }

        return null;
    }
}
