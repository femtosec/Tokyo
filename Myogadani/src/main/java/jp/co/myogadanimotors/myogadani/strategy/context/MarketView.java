package jp.co.myogadanimotors.myogadani.strategy.context;

import jp.co.myogadanimotors.myogadani.store.masterdata.market.BaseMarket;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.IMarket;
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketState;

public class MarketView extends BaseMarket {

    public MarketView(IMarket market) {
        id = market.getId();
        marketType = market.getMarketType();
        name = market.getName();
        mic = market.getMic();
        marketState = market.getMarketState();
    }

    public void setMarketState(MarketState marketState) {
        this.marketState = marketState;
    }
}
