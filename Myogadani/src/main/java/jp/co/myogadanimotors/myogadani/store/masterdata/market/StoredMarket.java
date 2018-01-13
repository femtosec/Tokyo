package jp.co.myogadanimotors.myogadani.store.masterdata.market;

public class StoredMarket extends BaseMarket {

    public void setId(long id) {
        this.id = id;
    }

    public String getMarketTypeString() {
        return marketType.toString();
    }

    public void setMarketTypeString(String marketTypeString) {
        marketType = MarketType.valueOf(marketTypeString);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMic(String mic) {
        this.mic = mic;
    }

    public void setMarketState(MarketState marketState) {
        this.marketState = marketState;
    }
}
