package jp.co.myogadanimotors.yushima.subscriber;

import jp.co.myogadanimotors.yushima.event.BitFlyerRawData;
import jp.co.myogadanimotors.yushima.event.IAsyncRawDataListener;
import jp.co.myogadanimotors.yushima.event.ZaifRawData;

import java.util.concurrent.Executor;

public class ListenerTest implements IAsyncRawDataListener {

    private final Executor eventQueue;

    public ListenerTest(Executor eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public void processBitFlyerRawData(BitFlyerRawData bitFlyerRawData) {
        System.out.println(bitFlyerRawData.getMarketDataType() + " // " + bitFlyerRawData.getRawData());
    }

    @Override
    public void processZaifRawData(ZaifRawData zaifRawData) {
        System.out.println(zaifRawData.getMarketDataType() + " // " + zaifRawData.getRawData());
    }

    @Override
    public Executor getEventQueue() {
        return eventQueue;
    }
}
