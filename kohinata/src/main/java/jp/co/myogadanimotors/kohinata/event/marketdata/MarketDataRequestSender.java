package jp.co.myogadanimotors.kohinata.event.marketdata;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

public class MarketDataRequestSender extends BaseEventSender<IAsyncMarketDataRequestListener> {

    public MarketDataRequestSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendMarketDataRequest(long requestId, long productId, String symbol, String mic) {
        send((eventId, creationTime, asyncEventListener) ->
                new MarketDataRequest(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        productId,
                        symbol,
                        mic
                )
        );
    }
}
