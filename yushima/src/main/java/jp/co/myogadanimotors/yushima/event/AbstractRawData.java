package jp.co.myogadanimotors.yushima.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;
import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;

import java.util.Objects;

public abstract class AbstractRawData<T> extends BaseEvent<IAsyncRawDataListener> {

    private final MarketDataType marketDataType;
    private final String symbol;
    private final String name;
    private final String mic;
    private final T rawData;

    public AbstractRawData(long eventId,
                           long creationTime,
                           IAsyncRawDataListener eventListener,
                           MarketDataType marketDataType,
                           String symbol,
                           String name,
                           String mic,
                           T rawData) {
        super(eventId, creationTime, eventListener);
        this.marketDataType = Objects.requireNonNull(marketDataType);
        this.symbol = Objects.requireNonNull(symbol);
        this.name = Objects.requireNonNull(name);
        this.mic = Objects.requireNonNull(mic);
        this.rawData = Objects.requireNonNull(rawData);
    }

    public MarketDataType getMarketDataType() {
        return marketDataType;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getMic() {
        return mic;
    }

    public T getRawData() {
        return rawData;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", marketDataType: ").append(marketDataType)
                .append(", symbol: ").append(symbol)
                .append(", name: ").append(name)
                .append(", mic: ").append(mic)
                .append(", rawData: ").append(rawData);
    }
}
