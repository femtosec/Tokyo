package jp.co.myogadanimotors.yushima.parser;

import jp.co.myogadanimotors.yushima.event.AbstractRawData;
import jp.co.myogadanimotors.yushima.mdmanager.IMarketDataEntry;
import jp.co.myogadanimotors.yushima.mdmanager.MarketDataEntry;

public interface IMarketDataParser<T extends AbstractRawData> {
    MarketDataEntry parse(T rawData, MarketDataEntry marketDataEntry);
}
