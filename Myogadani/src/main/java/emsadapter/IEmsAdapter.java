package emsadapter;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.amendorder.AmendOrder;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.cancelorder.CancelOrder;
import jp.co.myogadanimotors.myogadani.eventprocessing.order.neworder.NewOrder;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.amendack.AmendAck;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.amendreject.AmendReject;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.cancelack.CancelAck;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.cancelreject.CancelReject;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.fill.FillEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.newack.NewAck;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.newrejet.NewReject;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.unsolicitedcancel.UnsolicitedCancel;

import java.util.concurrent.Executor;

public interface IEmsAdapter {
    Executor getExecutor();
    void processNewAck(NewAck newAck);
    void processNewReject(NewReject newReject);
    void processAmendAck(AmendAck amendAck);
    void processAmendReject(AmendReject amendReject);
    void processCancelAck(CancelAck cancelAck);
    void processCancelReject(CancelReject cancelReject);
    void processUnsolicitedCancel(UnsolicitedCancel unsolicitedCancel);
    void processFill(FillEvent fillEvent);
    void addEventListeners(IAsyncEventListener<NewOrder> newOrderListener,
                           IAsyncEventListener<AmendOrder> amendOrderListener,
                           IAsyncEventListener<CancelOrder> cancelOrderListener,
                           Executor executor);
}
