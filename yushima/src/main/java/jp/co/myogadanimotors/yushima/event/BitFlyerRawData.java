package jp.co.myogadanimotors.yushima.event;

import com.google.gson.JsonElement;
import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;

import java.util.Objects;

public class BitFlyerRawData extends BaseEvent<IAsyncRawDataListener> {

    private final MarketDataType marketDataType;
    private final String symbol;
    private final String mic;
    private final JsonElement jsonElement;

    public BitFlyerRawData(long eventId,
                           long creationTime,
                           IAsyncRawDataListener eventListener,
                           MarketDataType marketDataType,
                           String symbol,
                           String mic,
                           JsonElement jsonElement) {
        super(eventId, creationTime, eventListener);
        this.marketDataType = Objects.requireNonNull(marketDataType);
        this.symbol = Objects.requireNonNull(symbol);
        this.mic = Objects.requireNonNull(mic);
        this.jsonElement = Objects.requireNonNull(jsonElement);
    }

    public MarketDataType getMarketDataType() {
        return marketDataType;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getMic() {
        return mic;
    }

    public JsonElement getRawData() {
        return jsonElement;
    }

    @Override
    protected void callEventListener(IAsyncRawDataListener eventListener) {
        eventListener.processBitFlyerRawData(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", marketDataType: ").append(marketDataType)
                .append(", symbol: ").append(symbol)
                .append(", mic: ").append(mic)
                .append(", jsonElement: ").append(jsonElement);
    }
}
