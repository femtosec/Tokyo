package jp.co.myogadanimotors.yushima.event;

import com.google.gson.JsonElement;
import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;

public class BitFlyerRawData extends AbstractRawData<JsonElement> {

    public BitFlyerRawData(long eventId,
                           long creationTime,
                           IAsyncRawDataListener eventListener,
                           MarketDataType marketDataType,
                           String symbol,
                           String name,
                           String mic,
                           JsonElement jsonElement) {
        super(eventId, creationTime, eventListener, marketDataType, symbol, name, mic, jsonElement);
    }

    @Override
    protected void callEventListener(IAsyncRawDataListener eventListener) {
        eventListener.processBitFlyerRawData(this);
    }
}
