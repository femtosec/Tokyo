package jp.co.myogadanimotors.yushima.marketdatamanager;

import jp.co.myogadanimotors.yushima.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executor;

public class MarketDataManager implements IAsyncSubscriptionRequestListener, IAsyncRawDataListener {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final Executor eventQueue;

    public MarketDataManager(Executor eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public Executor getEventQueue() {
        return eventQueue;
    }

    @Override
    public void processSubscriptionRequest(SubscriptionRequest subscriptionRequest) {
        logger.trace("processing subscription request.");
    }

    @Override
    public void processBitFlyerRawData(BitFlyerRawData bitFlyerRawData) {
        logger.trace("processing raw data.");
    }

    @Override
    public void processZaifRawData(ZaifRawData zaifRawData) {
        logger.trace("processing raw data.");
    }
}
