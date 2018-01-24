package jp.co.myogadanimotors.myogadani.store.masterdata.extendedattriute;

import jp.co.myogadanimotors.myogadani.store.masterdata.AbstractMasterDataStore;

public final class ExtendedAttributeMaster extends AbstractMasterDataStore<IExtendedAttributeDescriptor> {

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
