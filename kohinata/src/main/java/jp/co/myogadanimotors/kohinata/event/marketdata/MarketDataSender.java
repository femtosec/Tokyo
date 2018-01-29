package jp.co.myogadanimotors.kohinata.event.marketdata;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

import java.math.BigDecimal;

public class MarketDataSender extends BaseEventSender<IAsyncMarketDataListener> {

    private long requestId;
    private long productId;
    private String symbol;
    private String mic;
    private BigDecimal lastPrice;
    private BigDecimal lastTradeVolume;
    private DepthEntry[] bidDepth;
    private DepthEntry[] offerDepth;

    public MarketDataSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendMarketData(long requestId,
                               long productId,
                               String symbol,
                               String mic,
                               BigDecimal lastPrice,
                               BigDecimal lastTradeVolume,
                               DepthEntry[] bidDepth,
                               DepthEntry[] offerDepth) {
        this.requestId = requestId;
        this.productId = productId;
        this.symbol = symbol;
        this.mic = mic;
        this.lastPrice = lastPrice;
        this.lastTradeVolume = lastTradeVolume;
        this.bidDepth = bidDepth;
        this.offerDepth = offerDepth;
        send(this::createMarketData);
    }

    private IEvent createMarketData(long eventId, long creationTime, IAsyncMarketDataListener asyncEventListener) {
        return new MarketData(
                eventId,
                creationTime,
                requestId,
                productId,
                symbol,
                mic,
                lastPrice,
                lastTradeVolume,
                bidDepth,
                offerDepth,
                asyncEventListener
        );
    }
}
