package jp.co.myogadanimotors.kohinata.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

public class ReportSender extends BaseEventSender<IAsyncReportListener> {

    public ReportSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    public void sendNewAck(long requestId, long orderId) {
        send((eventId, creationTime, asyncEventListener) ->
                new NewAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderId
                )
        );
    }

    public void sendNewReject(long requestId, long orderId, String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new NewReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderId,
                        message
                )
        );
    }

    public void sendAmendAck(long requestId, long orderId) {
        send((eventId, creationTime, asyncEventListener) ->
                new AmendAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderId
                )
        );
    }

    public void sendAmendReject(long requestId, long orderId, String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new AmendReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderId,
                        message
                )
        );
    }

    public void sendCancelAck(long requestId, long orderId) {
        send((eventId, creationTime, asyncEventListener) ->
                new CancelAck(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderId
                )
        );
    }

    public void sendCancelReject(long requestId, long orderId, String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new CancelReject(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        requestId,
                        orderId,
                        message
                )
        );
    }

    public void sendUnsolicitedCancel(long orderId, String message) {
        send((eventId, creationTime, asyncEventListener) ->
                new UnsolicitedCancel(
                        eventId,
                        creationTime,
                        asyncEventListener,
                        orderId,
                        message
                )
        );
    }
}
