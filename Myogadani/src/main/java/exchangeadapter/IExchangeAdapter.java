package exchangeadapter;

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

public interface IExchangeAdapter {
    Executor getExecutor();
    void processNewOrder(NewOrder newOrder);
    void processAmendOrder(AmendOrder amendOrder);
    void processCancelOrder(CancelOrder cancelOrder);
    void addEventListeners(IAsyncEventListener<NewAck> newAckListener,
                           IAsyncEventListener<NewReject> newRejectListener,
                           IAsyncEventListener<AmendAck> amendAckListener,
                           IAsyncEventListener<AmendReject> amendRejectListener,
                           IAsyncEventListener<CancelAck> cancelAckListener,
                           IAsyncEventListener<CancelReject> cancelRejectListener,
                           IAsyncEventListener<UnsolicitedCancel> unsolicitedCancelListener,
                           IAsyncEventListener<FillEvent> fillEventListener,
                           Executor executor);
}
