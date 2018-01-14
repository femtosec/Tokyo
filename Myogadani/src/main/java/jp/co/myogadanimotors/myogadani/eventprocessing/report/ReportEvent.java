package jp.co.myogadanimotors.myogadani.eventprocessing.report;

import jp.co.myogadanimotors.myogadani.eventprocessing.AbstractEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.EventType;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderDestination;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.Orderer;

public final class ReportEvent extends AbstractEvent {

    private final ReportType reportType;
    private final long orderId;
    private final long requestId;
    private final String message;
    private final Orderer orderer;
    private final OrderDestination destination;

    public ReportEvent(long eventId,
                       long creationTime,
                       String eventSenderName,
                       ReportType reportType,
                       long orderId,
                       long requestId,
                       String message,
                       Orderer orderer,
                       OrderDestination destination) {
        super(eventId, creationTime, eventSenderName);
        this.reportType = reportType;
        this.orderId = orderId;
        this.requestId = requestId;
        this.message = message;
        this.orderer = orderer;
        this.destination = destination;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getRequestId() {
        return requestId;
    }

    public String getMessage() {
        return message;
    }

    public Orderer getOrderer() {
        return orderer;
    }

    public OrderDestination getDestination() {
        return destination;
    }

    @Override
    public EventType getEventType() {
        return EventType.Report;
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", orderId: ").append(orderId)
                .append(", requestId: ").append(requestId)
                .append(", reportType: ").append(reportType)
                .append(", message: ").append(message)
                .append(", orderer: ").append(orderer)
                .append(", destination: ").append(destination);
    }
}
