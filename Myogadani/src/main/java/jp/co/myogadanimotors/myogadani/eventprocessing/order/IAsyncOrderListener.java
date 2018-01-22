package jp.co.myogadanimotors.myogadani.eventprocessing.order;

import jp.co.myogadanimotors.myogadani.eventprocessing.IAsyncEventListener;

public interface IAsyncOrderListener extends IAsyncEventListener {
    void processNewOrder(NewOrder newOrderEvent);
    void processAmendOrder(AmendOrder amendOrderEvent);
    void processCancelOrder(CancelOrder cancelOrderEvent);
}
