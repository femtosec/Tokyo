package jp.co.myogadanimotors.myogadani.store.masterdata.market;

import jp.co.myogadanimotors.myogadani.common.Constants;

public class Market implements IMarket {

    private long id = Constants.NOT_SET_ID_LONG;
    private MarketType marketType;
    private String name;
    private String mic;
    private MarketState marketState;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public MarketType getMarketType() {
        return marketType;
    }

    public String getMarketTypeString() {
        return marketType.toString();
    }

    public void setMarketTypeString(String marketTypeString) {
        marketType = MarketType.valueOf(marketTypeString);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMic() {
        return mic;
    }

    public void setMic(String mic) {
        this.mic = mic;
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
        StringBuilder sb = new StringBuilder();
        sb.append("marketId: ").append(id)
                .append(", marketType: ").append(marketType)
                .append(", name: ").append(name)
                .append(", mic: ").append(mic)
                .append(", marketState: ").append(marketState);
        return sb.toString();
    }
}
