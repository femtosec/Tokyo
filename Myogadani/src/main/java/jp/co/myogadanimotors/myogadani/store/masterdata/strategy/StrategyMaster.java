package jp.co.myogadanimotors.myogadani.store.masterdata.strategy;

import jp.co.myogadanimotors.myogadani.store.masterdata.AbstractMasterDataStore;

public class StrategyMaster extends AbstractMasterDataStore<StrategyDescriptor> {

    @Override
    protected StrategyDescriptor create() {
        return new StrategyDescriptor();
    }

    public StrategyDescriptor getByName(String name) {
        for (StrategyDescriptor sd : objectsById.values()) {
            if (sd.getName().equals(name)) return sd;
        }

        return null;
    }
}
