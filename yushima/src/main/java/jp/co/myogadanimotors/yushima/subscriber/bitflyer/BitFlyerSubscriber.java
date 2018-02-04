package jp.co.myogadanimotors.yushima.subscriber.bitflyer;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor;
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator;
import jp.co.myogadanimotors.bunkyo.master.MasterDataInitializeException;
import jp.co.myogadanimotors.bunkyo.master.market.IMarket;
import jp.co.myogadanimotors.bunkyo.master.market.MarketMaster;
import jp.co.myogadanimotors.bunkyo.master.product.IProduct;
import jp.co.myogadanimotors.bunkyo.master.product.ProductMaster;
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource;
import jp.co.myogadanimotors.yushima.event.IAsyncRawDataListener;
import jp.co.myogadanimotors.yushima.event.RawDataSender;
import jp.co.myogadanimotors.yushima.subscriber.IMarketDataSubscriber;
import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BitFlyerSubscriber implements IMarketDataSubscriber {

    private final Logger logger = LogManager.getLogger(getClass().getName());
    private final BitFlyerSubscriptionList subscriptionList = new BitFlyerSubscriptionList();
    private final List<PubNub> pubNubs = new ArrayList<>();
    private final RawDataSender rawDataSender;
    private final MarketMaster marketMaster;
    private final ProductMaster productMaster;
    private String subscribeKey;

    public BitFlyerSubscriber(EventIdGenerator idGenerator,
                              ITimeSource timeSource,
                              MarketMaster marketMaster,
                              ProductMaster productMaster) {
        this.rawDataSender = new RawDataSender(idGenerator, timeSource);
        this.marketMaster = marketMaster;
        this.productMaster = productMaster;
    }

    @Override
    public void addEventListener(IAsyncRawDataListener rawDataListener) {
        rawDataSender.addAsyncEventListener(Objects.requireNonNull(rawDataListener));
    }

    @Override
    public void init(ConfigAccessor configAccessor) throws MasterDataInitializeException {
        logger.info("initializing.");

        subscriptionList.init(configAccessor);

        subscribeKey = configAccessor.getString("bitflyer.subscribeKey");
        logger.info("bitFlyer subscription key: {}", subscribeKey);
    }

    @Override
    public void connect() {
        logger.info("connecting.");

        PNConfiguration pnConf = new PNConfiguration();
        pnConf.setSubscribeKey(subscribeKey);

        subscriptionList.forEach((subscription) -> {
            IProduct product = productMaster.get(subscription.getProductId());
            IMarket market = marketMaster.get(product.getMarketId());

            PubNub pubNub = new PubNub(pnConf);
            pubNub.addListener(
                    new Listener(
                            subscription.getMarketDataType(),
                            product.getSymbol(),
                            product.getName(),
                            market.getMic()
                    )
            );
            pubNub.subscribe().channels(Collections.singletonList(subscription.getChannel())).execute();
            pubNubs.add(pubNub);
        });
    }

    @Override
    public void disconnect() {
        logger.info("disconnecting.");

        for (PubNub pubNub: pubNubs) {
            pubNub.unsubscribe();
            pubNub.disconnect();
        }
    }

    private class Listener extends SubscribeCallback {

        private final MarketDataType marketDataType;
        private final String symbol;
        private final String name;
        private final String mic;

        private Listener(MarketDataType marketDataType, String symbol, String name, String mic) {
            this.marketDataType = marketDataType;
            this.symbol = symbol;
            this.name = name;
            this.mic = mic;
        }

        @Override
        public void status(PubNub pubnub, PNStatus status) {
            logger.info(status);
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {
            if (logger.getLevel() == Level.TRACE) {
                logger.trace("{} // {} // {} // {} // {} // {}", marketDataType, symbol, name, mic, message.getChannel(), message.getMessage());
            }
            rawDataSender.sendBitFlyerRawData(marketDataType, symbol, name, mic, message.getMessage());
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            logger.info(presence.getSubscription());
        }
    }
}
