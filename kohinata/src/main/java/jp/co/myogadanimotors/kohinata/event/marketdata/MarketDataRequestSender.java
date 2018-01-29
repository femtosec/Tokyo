package jp.co.myogadanimotors.kohinata.event.marketdata;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

public class MarketDataRequestSender extends BaseEventSender<IAsyncMarketDataRequestListener> {

    private long requestId;
    private long productId;
    private String symbol;
    private String mic;

    public MarketDataRequestSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendMarketDataRequest(long requestId, long productId, String symbol, String mic) {
        this.requestId = requestId;
        this.productId = productId;
        this.symbol = symbol;
        this.mic = mic;
        send(this::createMarketDataRequest);
    }

    private IEvent createMarketDataRequest(long eventId, long creationTime, IAsyncMarketDataRequestListener asyncEventListener) {
        return new MarketDataRequest(
                eventId,
                creationTime,
                requestId,
                productId,
                symbol,
                mic,
                asyncEventListener
        );
    }
}
