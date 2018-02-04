package jp.co.myogadanimotors.yushima.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

public class SubscriptionRequestSender extends BaseEventSender<IAsyncSubscriptionRequestListener> {

    private long requestId;
    private long productId;
    private String symbol;
    private String mic;

    public SubscriptionRequestSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendSubscriptionRequest(long requestId, long productId, String symbol, String mic) {
        this.requestId = requestId;
        this.productId = productId;
        this.symbol = symbol;
        this.mic = mic;
        send(this::createSubscriptionRequest);
    }

    private IEvent createSubscriptionRequest(long eventId, long creationTime, IAsyncSubscriptionRequestListener asyncEventListener) {
        return new SubscriptionRequest(
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
