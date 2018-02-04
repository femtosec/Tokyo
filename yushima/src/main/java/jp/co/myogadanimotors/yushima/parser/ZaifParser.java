package jp.co.myogadanimotors.yushima.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import jp.co.myogadanimotors.yushima.event.ZaifRawData;
import jp.co.myogadanimotors.yushima.mdmanager.IMarketDataEntry;
import jp.co.myogadanimotors.yushima.mdmanager.MarketDataEntry;

public class ZaifParser implements IMarketDataParser<ZaifRawData> {

    private final Gson gson = new Gson();

    public ZaifParser() {

    }

    @Override
    public MarketDataEntry parse(ZaifRawData rawData, MarketDataEntry marketDataEntry) {
        JsonElement jsonElement = gson.toJsonTree(rawData.getRawData(), String.class);
        return marketDataEntry;
    }
}
