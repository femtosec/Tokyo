package jp.co.myogadanimotors.kohinata.event.report;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEventSender;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.eventprocessing.IEvent;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;

public class ReportSender extends BaseEventSender<IAsyncReportListener> {

    private long requestId;
    private long orderId;
    private String message;

    public ReportSender(EventIdGenerator idGenerator, ITimeSource timeSource) {
        super(idGenerator, timeSource);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // event senders
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendNewAck(long requestId, long orderId) {
        this.requestId = requestId;
        this.orderId = orderId;
        send(this::createNewAck);
    }

    public void sendNewReject(long requestId, long orderId, String message) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.message = message;
        send(this::createNewReject);
    }

    public void sendAmendAck(long requestId, long orderId) {
        this.requestId = requestId;
        this.orderId = orderId;
        send(this::createAmendAck);
    }

    public void sendAmendReject(long requestId, long orderId, String message) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.message = message;
        send(this::createAmendReject);
    }

    public void sendCancelAck(long requestId, long orderId) {
        this.requestId = requestId;
        this.orderId = orderId;
        send(this::createCancelAck);
    }

    public void sendCancelReject(long requestId, long orderId, String message) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.message = message;
        send(this::createCancelReject);
    }

    public void sendUnsolicitedCancel(long orderId, String message) {
        this.orderId = orderId;
        this.message = message;
        send(this::createUnsolicitedCancel);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // event factory methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private IEvent createNewAck(long eventId, long creationTime, IAsyncReportListener asyncReportListener) {
        return new NewAck(eventId, creationTime, requestId, orderId, asyncReportListener);
    }

    private IEvent createNewReject(long eventId, long creationTime, IAsyncReportListener asyncReportListener) {
        return new NewReject(
                eventId,
                creationTime,
                requestId,
                orderId,
                message,
                asyncReportListener
        );
    }

    private IEvent createAmendAck(long eventId, long creationTime, IAsyncReportListener asyncReportListener) {
        return new AmendAck(eventId, creationTime, requestId, orderId, asyncReportListener);
    }

    private IEvent createAmendReject(long eventId, long creationTime, IAsyncReportListener asyncReportListener) {
        return new AmendReject(
                eventId,
                creationTime,
                requestId,
                orderId,
                message,
                asyncReportListener
        );
    }

    private IEvent createCancelAck(long eventId, long creationTime, IAsyncReportListener asyncReportListener) {
        return new CancelAck(eventId, creationTime, requestId, orderId, asyncReportListener);
    }

    private IEvent createCancelReject(long eventId, long creationTime, IAsyncReportListener asyncReportListener) {
        return new CancelReject(
                eventId,
                creationTime,
                requestId,
                orderId,
                message,
                asyncReportListener
        );
    }

    private IEvent createUnsolicitedCancel(long eventId, long creationTime, IAsyncReportListener asyncReportListener) {
        return new UnsolicitedCancel(eventId, creationTime, orderId, message, asyncReportListener);
    }
}
