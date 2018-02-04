package jp.co.myogadanimotors.yushima.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;

import java.util.Objects;

public class ZaifRawData extends BaseEvent<IAsyncRawDataListener> {

    private final MarketDataType marketDataType;
    private final String symbol;
    private final String mic;
    private final String rawData;

    public ZaifRawData(long eventId,
                       long creationTime,
                       IAsyncRawDataListener eventListener,
                       MarketDataType marketDataType,
                       String symbol,
                       String mic,
                       String rawData) {
        super(eventId, creationTime, eventListener);
        this.marketDataType = Objects.requireNonNull(marketDataType);
        this.symbol = Objects.requireNonNull(symbol);
        this.mic = Objects.requireNonNull(mic);
        this.rawData = Objects.requireNonNull(rawData);
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

    public String getRawData() {
        return rawData;
    }

    @Override
    protected void callEventListener(IAsyncRawDataListener eventListener) {
        eventListener.processZaifRawData(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", marketDataType: ").append(marketDataType)
                .append(", symbol: ").append(symbol)
                .append(", mic: ").append(mic)
                .append(", rawData: ").append(rawData);
    }
}
