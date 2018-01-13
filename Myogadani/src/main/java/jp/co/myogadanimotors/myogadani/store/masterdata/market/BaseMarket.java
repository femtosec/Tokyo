package jp.co.myogadanimotors.myogadani.store.masterdata.market;

import jp.co.myogadanimotors.myogadani.common.Constants;

public class BaseMarket implements IMarket {

    protected long id = Constants.NOT_SET_ID_LONG;
    protected MarketType marketType;
    protected String name;
    protected String mic;
    protected MarketState marketState;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("marketId: ").append(id)
                .append(", marketType: ").append(marketType)
                .append(", name: ").append(name)
                .append(", mic: ").append(mic)
                .append(", marketState: ").append(marketState);
        return sb.toString();
    }
}
