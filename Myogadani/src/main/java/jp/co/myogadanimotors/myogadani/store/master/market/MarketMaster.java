package jp.co.myogadanimotors.myogadani.store.master.market;

import jp.co.myogadanimotors.myogadani.store.master.AbstractDataMaster;

public final class MarketMaster extends AbstractDataMaster<IMarket> {

    @Override
    protected IMarket create() {
        return new Market();
    }

    public IMarket getByMic(String mic) {
        for (IMarket market : objectsById.values()) {
            if (market.getMic().equals(mic)) return market;
        }

        return null;
    }
}
