package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.store.master.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.master.market.MarketState;
import jp.co.myogadanimotors.myogadani.store.master.market.MarketType;

public final class MarketView implements IMarket {

    private final long id;
    private final MarketType marketType;
    private final String name;
    private final String mic;
    private MarketState marketState;

    public MarketView(IMarket market) {
        id = market.getId();
        marketType = market.getMarketType();
        name = market.getName();
        mic = market.getMic();
        marketState = market.getMarketState();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public MarketType getMarketType() {
        return marketType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMic() {
        return mic;
    }

    @Override
    public MarketState getMarketState() {
        return marketState;
    }

    public void setMarketState(MarketState marketState) {
        this.marketState = marketState;
    }

    @Override
    public String toString() {
        return "marketId: " + id +
                ", marketType: " + marketType +
                ", name: " + name +
                ", mic: " + mic +
                ", marketState: " + marketState;
    }
}
