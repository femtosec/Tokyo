package jp.co.myogadanimotors.myogadani.store.masterdata.market;

import jp.co.myogadanimotors.myogadani.store.masterdata.AbstractMasterDataStore;

public final class MarketMaster extends AbstractMasterDataStore<StoredMarket> {

    @Override
    protected StoredMarket create() {
        return new StoredMarket();
    }

    public StoredMarket getByMic(String mic) {
        for (StoredMarket market : objectsById.values()) {
            if (market.getMic() == mic) return market;
        }

        return null;
    }
}
