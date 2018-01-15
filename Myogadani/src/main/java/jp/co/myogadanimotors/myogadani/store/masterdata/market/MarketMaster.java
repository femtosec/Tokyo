package jp.co.myogadanimotors.myogadani.store.masterdata.market;

import jp.co.myogadanimotors.myogadani.store.masterdata.AbstractMasterDataStore;

public final class MarketMaster extends AbstractMasterDataStore<Market> {

    @Override
    protected Market create() {
        return new Market();
    }

    public Market getByMic(String mic) {
        for (Market market : objectsById.values()) {
            if (market.getMic().equals(mic)) return market;
        }

        return null;
    }
}
