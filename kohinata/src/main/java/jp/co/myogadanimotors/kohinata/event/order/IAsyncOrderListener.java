package jp.co.myogadanimotors.kohinata.event.order;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncOrderListener extends IAsyncEventListener {
    void processNewOrder(NewOrder newOrderEvent);
    void processAmendOrder(AmendOrder amendOrderEvent);
    void processCancelOrder(CancelOrder cancelOrderEvent);
}
