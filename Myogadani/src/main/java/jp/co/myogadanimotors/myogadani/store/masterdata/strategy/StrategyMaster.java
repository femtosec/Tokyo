package jp.co.myogadanimotors.myogadani.store.masterdata.strategy;

import jp.co.myogadanimotors.myogadani.store.masterdata.AbstractMasterDataStore;

public final class StrategyMaster extends AbstractMasterDataStore<IStrategyDescriptor> {

    @Override
    protected IStrategyDescriptor create() {
        return new StrategyDescriptor();
    }

    public IStrategyDescriptor getByName(String name) {
        for (IStrategyDescriptor sd : objectsById.values()) {
            if (sd.getName().equals(name)) return sd;
        }

        return null;
    }
}
