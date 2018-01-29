package jp.co.myogadanimotors.kohinata.master.strategy;

import jp.co.myogadanimotors.bunkyo.master.AbstractMaster;

public final class StrategyMaster extends AbstractMaster<IStrategyDescriptor> {

    @Override
    protected IStrategyDescriptor create() {
        return new StrategyDescriptor();
    }

    public IStrategyDescriptor getByName(String name) {
        return get(sd -> sd.getName().equals(name));
    }
}
