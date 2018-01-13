package jp.co.myogadanimotors.myogadani.eventprocessing.orderevent;

import jp.co.myogadanimotors.myogadani.eventprocessing.IEventListener;

public interface IOrderEventListener extends IEventListener {
    void processOrderNew(OrderEvent orderEvent);
    void processOrderAmend(OrderEvent orderEvent);
    void processOrderCancel(OrderEvent orderEvent);
}
