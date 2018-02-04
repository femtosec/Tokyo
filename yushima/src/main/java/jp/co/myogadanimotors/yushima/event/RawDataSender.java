package jp.co.myogadanimotors.yushima.event;

import com.google.gson.JsonElement;
import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;

public class RawDataSender extends BaseEventSender<IAsyncRawDataListener> {

    private MarketDataType marketDataType;
    private String symbol;
    private String mic;
    private JsonElement jsonElement;
    private String zaifRawData;

    public RawDataSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendBitFlyerRawData(MarketDataType marketDataType, String symbol, String mic, JsonElement jsonElement) {
        this.marketDataType = marketDataType;
        this.symbol = symbol;
        this.mic = mic;
        this.jsonElement = jsonElement;
        send(this::createBitFlyerRawData);
    }

    public void sendZaifRawData(MarketDataType marketDataType, String symbol, String mic, String rawData) {
        this.marketDataType = marketDataType;
        this.symbol = symbol;
        this.mic = mic;
        this.zaifRawData = rawData;
        send(this::createZaifRawData);
    }

    private IEvent createBitFlyerRawData(long eventId, long creationTime, IAsyncRawDataListener asyncEventListener) {
        return new BitFlyerRawData(
                eventId,
                creationTime,
                asyncEventListener,
                marketDataType,
                symbol,
                mic,
                jsonElement
        );
    }

    private IEvent createZaifRawData(long eventId, long creationTime, IAsyncRawDataListener asyncEventListener) {
        return new ZaifRawData(
                eventId,
                creationTime,
                asyncEventListener,
                marketDataType,
                symbol,
                mic,
                zaifRawData
        );
    }
}
