package jp.co.myogadanimotors.myogadani.store.masterdata.market;

import jp.co.myogadanimotors.myogadani.store.masterdata.AbstractMasterDataStore;

public final class MarketMaster extends AbstractMasterDataStore<IMarket> {

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
