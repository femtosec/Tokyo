package jp.co.myogadanimotors.myogadani.eventprocessing.report;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public interface IAsyncReportListener extends IAsyncEventListener {
    void processNewAck(NewAck newAck);
    void processNewReject(NewReject newReject);
    void processAmendAck(AmendAck amendAck);
    void processAmendReject(AmendReject amendReject);
    void processCancelAck(CancelAck cancelAck);
    void processCancelReject(CancelReject cancelReject);
    void processUnsolicitedCancel(UnsolicitedCancel unsolicitedCancel);
}
