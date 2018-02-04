package jp.co.myogadanimotors.bunkyo.master.market;

import jp.co.myogadanimotors.bunkyo.master.AbstractMaster;

public final class MarketMaster extends AbstractMaster<IMarket> {

    @Override
    protected IMarket create() {
        return new Market();
    }

    public IMarket getByMic(String mic) {
        return get(market -> market.getMic().equals(mic));
    }
}
