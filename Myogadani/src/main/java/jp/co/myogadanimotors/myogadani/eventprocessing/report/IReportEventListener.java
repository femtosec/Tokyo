package jp.co.myogadanimotors.myogadani.eventprocessing.report;

import jp.co.myogadanimotors.myogadani.eventprocessing.IEventListener;

public interface IReportEventListener extends IEventListener {
    void processReportNewAck(ReportEvent reportEvent);
    void processReportNewReject(ReportEvent reportEvent);
    void processReportAmendAck(ReportEvent reportEvent);
    void processReportAmendReject(ReportEvent reportEvent);
    void processReportCancelAck(ReportEvent reportEvent);
    void processReportCancelReject(ReportEvent reportEvent);
    void processReportUnsolicitedCancel(ReportEvent reportEvent);
}
