package jp.co.myogadanimotors.myogadani.master.extendedattriute;

import jp.co.myogadanimotors.bunkyo.master.AbstractMaster;

public final class ExtendedAttributeMaster extends AbstractMaster<IExtendedAttributeDescriptor> {

    @Override
    protected IExtendedAttributeDescriptor create() {
        return new ExtendedAttributeDescriptor();
    }

    public IExtendedAttributeDescriptor getByName(String name) {
        return get(ead -> ead.getName().equals(name));
    }
}
