package jp.co.myogadanimotors.myogadani.eventprocessing.order.cancelorder;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

public class CancelOrderSender extends BaseEventSender<CancelOrder> {

    private long requestId;
    private long orderId;

    public CancelOrderSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    @Override
    protected CancelOrder createEvent(long eventId, long creationTime, IAsyncEventListener<CancelOrder> eventListener) {
        return new CancelOrder(
                eventId,
                creationTime,
                requestId,
                orderId,
                eventListener
        );
    }

    public void sendCancelOrder(long requestId, long orderId) {
        this.requestId = requestId;
        this.orderId = orderId;
        send();
    }
}
