package jp.co.myogadanimotors.yushima.event;

import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;

public class ZaifRawData extends AbstractRawData<String> {

    public ZaifRawData(long eventId,
                       long creationTime,
                       IAsyncRawDataListener eventListener,
                       MarketDataType marketDataType,
                       String symbol,
                       String name,
                       String mic,
                       String rawData) {
        super(eventId, creationTime, eventListener, marketDataType, symbol, name, mic, rawData);
    }

    @Override
    protected void callEventListener(IAsyncRawDataListener eventListener) {
        eventListener.processZaifRawData(this);
    }
}
