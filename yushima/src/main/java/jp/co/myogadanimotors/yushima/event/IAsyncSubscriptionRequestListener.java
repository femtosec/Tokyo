package jp.co.myogadanimotors.yushima.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncSubscriptionRequestListener extends IAsyncEventListener {
    void processSubscriptionRequest(SubscriptionRequest subscriptionRequest);
}
