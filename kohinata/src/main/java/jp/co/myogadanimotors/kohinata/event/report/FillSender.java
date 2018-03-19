package jp.co.myogadanimotors.kohinata.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

import java.math.BigDecimal;

public class FillSender extends BaseEventSender<IAsyncFillListener> {

    public FillSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendFill(long orderId, BigDecimal execQuantity) {
        send((eventId, creationTime, asyncEventListener) ->
                new FillEvent(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderId,
                        execQuantity
                )
        );
    }
}
