package jp.co.myogadanimotors.myogadani.store.master.strategy;

import jp.co.myogadanimotors.myogadani.store.master.AbstractDataMaster;

public final class StrategyMaster extends AbstractDataMaster<IStrategyDescriptor> {

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
