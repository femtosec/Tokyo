package jp.co.myogadanimotors.yushima.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.IAsyncEventListener;

public interface IAsyncRawDataListener extends IAsyncEventListener {
    void processBitFlyerRawData(BitFlyerRawData bitFlyerRawData);
    void processZaifRawData(ZaifRawData zaifRawData);
}
