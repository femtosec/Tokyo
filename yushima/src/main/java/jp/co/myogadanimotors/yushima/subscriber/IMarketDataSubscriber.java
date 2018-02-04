package jp.co.myogadanimotors.yushima.subscriber;

import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor;
import jp.co.myogadanimotors.bunkyo.master.MasterDataInitializeException;
import jp.co.myogadanimotors.yushima.event.IAsyncRawDataListener;

public interface IMarketDataSubscriber {
    void addEventListener(IAsyncRawDataListener rawDataListener);
    void init(ConfigAccessor configAccessor) throws MasterDataInitializeException;
    void connect();
    void disconnect();
}
