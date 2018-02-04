package jp.co.myogadanimotors.yushima.subscriber.zaif;

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

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ZaifSubscriber implements IMarketDataSubscriber {

    private final Logger logger = LogManager.getLogger(getClass().getName());
    private final ZaifSubscriptionList subscriptionList = new ZaifSubscriptionList();
    private final List<Session> sessions = new ArrayList<>();
    private final RawDataSender rawDataSender;
    private final MarketMaster marketMaster;
    private final ProductMaster productMaster;
    private String urlPrefix;

    public ZaifSubscriber(EventIdGenerator idGenerator,
                          ITimeSource timeSource,
                          MarketMaster marketMaster,
                          ProductMaster productMaster) {
        this.rawDataSender = new RawDataSender(idGenerator, timeSource);
        this.marketMaster = marketMaster;
        this.productMaster = productMaster;
    }

    @Override
    public void addEventListener(IAsyncRawDataListener rawDataListener) {
        rawDataSender.addAsyncEventListener(rawDataListener);
    }

    @Override
    public void init(ConfigAccessor configAccessor) throws MasterDataInitializeException  {
        logger.info("initializing.");

        subscriptionList.init(configAccessor);

        urlPrefix = configAccessor.getString("zaif.apiUrlPrefix");
        logger.info("zaif urlPrefix: {}", urlPrefix);
    }

    @Override
    public void connect() {
        logger.info("connecting.");

        WebSocketContainer wsc = ContainerProvider.getWebSocketContainer();

        subscriptionList.forEach(subscription -> {
            try {
                URI uri = URI.create(urlPrefix + subscription.getCurrencyPair());
                IProduct product = productMaster.get(subscription.getProductId());
                IMarket market = marketMaster.get(product.getMarketId());

                sessions.add(wsc.connectToServer(new Listener(subscription.getMarketDataType(), product.getSymbol(), market.getMic()), uri));
            } catch (DeploymentException | IOException e) {
                e.printStackTrace();
                disconnect();
            }
        });
    }

    @Override
    public void disconnect() {
        logger.info("disconnecting.");

        for (Session session: sessions) {
            try {
                session.close();
            } catch (IOException e) {
                logger.error("session disconnection failed.", e);
            }
        }

        sessions.clear();
    }

    @ClientEndpoint
    public final class Listener {

        private final MarketDataType marketDataType;
        private final String symbol;
        private final String mic;

        private Listener(MarketDataType marketDataType, String symbol, String mic) {
            this.marketDataType = marketDataType;
            this.symbol = symbol;
            this.mic = mic;
        }

        @OnOpen
        public void onOpen(Session session) {
            logger.info("session opened. (session: {})", session);
        }

        @OnMessage
        public void onMessage(String message) {
            if (logger.getLevel() == Level.TRACE) {
                logger.trace("{} // {} // {} // {}", marketDataType, symbol, mic, message);
            }
            rawDataSender.sendZaifRawData(marketDataType, symbol, mic, message);
        }

        @OnError
        public void onError(Throwable t) {
            logger.error(t);
        }

        @OnClose
        public void onClose(Session session) {
            logger.info("session closed. (session: {})", session);
        }
    }
}
