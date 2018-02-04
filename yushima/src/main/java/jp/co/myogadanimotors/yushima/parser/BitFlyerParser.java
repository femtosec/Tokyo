package jp.co.myogadanimotors.yushima.parser;

import jp.co.myogadanimotors.yushima.event.BitFlyerRawData;
import jp.co.myogadanimotors.yushima.mdmanager.MarketDataEntry;

public class BitFlyerParser implements IMarketDataParser<BitFlyerRawData> {

    public BitFlyerParser() {

    }

    @Override
    public MarketDataEntry parse(BitFlyerRawData rawData, MarketDataEntry marketDataEntry) {
        return marketDataEntry;
    }
}
