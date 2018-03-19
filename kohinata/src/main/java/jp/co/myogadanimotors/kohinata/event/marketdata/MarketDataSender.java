package jp.co.myogadanimotors.kohinata.event.marketdata;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

import java.math.BigDecimal;

public class MarketDataSender extends BaseEventSender<IAsyncMarketDataListener> {

    public MarketDataSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendMarketData(long requestId,
                               long productId,
                               String symbol,
                               String name,
                               String mic,
                               BigDecimal lastPrice,
                               BigDecimal lastTradeVolume,
                               DepthEntry[] bidDepth,
                               DepthEntry[] offerDepth) {
        send((eventId, creationTime, asyncEventListener) ->
                new MarketData(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        productId,
                        symbol,
                        name,
                        mic,
                        lastPrice,
                        lastTradeVolume,
                        bidDepth,
                        offerDepth
                )
        );
    }
}
