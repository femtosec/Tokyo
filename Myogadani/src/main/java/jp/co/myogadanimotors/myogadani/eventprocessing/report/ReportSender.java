package jp.co.myogadanimotors.myogadani.eventprocessing.report;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEventSender;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventIdGenerator;
import jp.co.myogadanimotors.myogadani.eventprocessing.IEventStream;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;
import jp.co.myogadanimotors.myogadani.timesource.ITimeSource;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class ReportSender extends AbstractEventSender<ReportEvent> {

    private ReportType reportType;
    private long orderId;
    private long requestId;
    private String message;
    private Orderer orderer;
    private OrderDestination destination;

    public ReportSender(String eventSenderName, IEventIdGenerator idGenerator, ITimeSource timeSource, IEventStream... eventStreams) {
        super(eventSenderName, idGenerator, timeSource, eventStreams);
    }

    @Override
    protected ReportEvent createEvent() {
        return new ReportEvent(
                generateEventId(),
                getCurrentTime(),
                getEventSenderName(),
                reportType,
                orderId,
                requestId,
                message,
                orderer,
                destination
        );
    }

    private void sendParentOrderReport(ReportType reportType,
                                       long orderId,
                                       long requestId,
                                       String message,
                                       Orderer orderer,
                                       OrderDestination destination) {
        this.reportType = reportType;
        this.orderId = orderId;
        this.requestId = requestId;
        this.message = message;
        this.orderer = orderer;
        this.destination = destination;
        send();
    }

    public final void sendNewAck(long orderId,
                                 long requestId,
                                 Orderer orderer,
                                 OrderDestination destination) {
        sendParentOrderReport(
                ReportType.NewAck,
                orderId,
                requestId,
                "NA",
                orderer,
                destination
        );
    }

    public final void sendNewReject(long orderId,
                                    long requestId,
                                    String rejectReason,
                                    Orderer orderer,
                                    OrderDestination destination) {
        sendParentOrderReport(
                ReportType.NewReject,
                orderId,
                requestId,
                rejectReason,
                orderer,
                destination
        );
    }

    public final void sendAmendAck(long orderId,
                                   long requestId,
                                   Orderer orderer,
                                   OrderDestination destination) {
        sendParentOrderReport(
                ReportType.AmendAck,
                orderId,
                requestId,
                "NA",
                orderer,
                destination
        );
    }

    public final void sendAmendReject(long orderId,
                                      long requestId,
                                      String rejectReason,
                                      Orderer orderer,
                                      OrderDestination destination) {
        sendParentOrderReport(
                ReportType.AmendReject,
                orderId,
                requestId,
                rejectReason,
                orderer,
                destination
        );
    }

    public final void sendCancelAck(long orderId,
                                    long requestId,
                                    Orderer orderer,
                                    OrderDestination destination) {
        sendParentOrderReport(
                ReportType.CancelAck,
                orderId,
                requestId,
                "NA",
                orderer,
                destination
        );
    }

    public final void sendCancelReject(long orderId,
                                       long requestId,
                                       String rejectReason,
                                       Orderer orderer,
                                       OrderDestination destination) {
        sendParentOrderReport(
                ReportType.CancelReject,
                orderId,
                requestId,
                rejectReason,
                orderer,
                destination
        );
    }

    public final void sendUnsolicitedCancel(long orderId,
                                            long requestId,
                                            String cancelReason,
                                            Orderer orderer,
                                            OrderDestination destination) {
        sendParentOrderReport(
                ReportType.UnsolicitedCancel,
                orderId,
                requestId,
                cancelReason,
                orderer,
                destination
        );
    }
}
