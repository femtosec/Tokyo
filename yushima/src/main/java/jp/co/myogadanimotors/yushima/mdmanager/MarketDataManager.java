package jp.co.myogadanimotors.yushima.mdmanager;

import jp.co.myogadanimotors.yushima.event.BitFlyerRawData;
import jp.co.myogadanimotors.yushima.event.IAsyncRawDataListener;
import jp.co.myogadanimotors.yushima.event.ZaifRawData;
import jp.co.myogadanimotors.yushima.parser.BitFlyerParser;
import jp.co.myogadanimotors.yushima.parser.ZaifParser;
import jp.co.myogadanimotors.yushima.publisher.IMarketDataPublisher;
import jp.co.myogadanimotors.yushima.publisher.IMarketDataPublisherFactory;
import jp.co.myogadanimotors.yushima.publisher.MarketDataPublisherException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executor;

public class MarketDataManager implements IAsyncRawDataListener {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    private final IMarketDataPublisherFactory marketDataPublisherFactory;
    private final int maxNumberOfDepths;
    private final BitFlyerParser bitFlyerParser = new BitFlyerParser();
    private final ZaifParser zaifParser = new ZaifParser();
    private final Executor eventQueue;
    private IMarketDataPublisher bitFlyerPublisher;
    private IMarketDataPublisher zaifPublisher;
    private MarketDataEntry bitFlyerMarketDataEntry;
    private MarketDataEntry zaifMarketDataEntry;

    public MarketDataManager(IMarketDataPublisherFactory marketDataPublisherFactory, int maxNumberOfDepths, Executor eventQueue) {
        this.marketDataPublisherFactory = marketDataPublisherFactory;
        this.maxNumberOfDepths = maxNumberOfDepths;
        this.eventQueue = eventQueue;
    }

    @Override
    public Executor getEventQueue() {
        return eventQueue;
    }

    @Override
    public void processBitFlyerRawData(BitFlyerRawData bitFlyerRawData) {
        logger.trace("processing raw data.");

        // update market data entry
        if (bitFlyerMarketDataEntry == null) {
            logger.info("creating a bitFlyer MarketDataEntry.");
            bitFlyerMarketDataEntry = new MarketDataEntry(
                    maxNumberOfDepths,
                    bitFlyerRawData.getSymbol(),
                    bitFlyerRawData.getName(),
                    bitFlyerRawData.getMic()
            );
        }

        bitFlyerMarketDataEntry = bitFlyerParser.parse(bitFlyerRawData, bitFlyerMarketDataEntry);

        // publish market data
        if (bitFlyerPublisher == null) {
            try {
                bitFlyerPublisher = marketDataPublisherFactory.create(bitFlyerMarketDataEntry.getSymbol());
            } catch (MarketDataPublisherException e) {
                logger.error(e.getMessage(), e);
                return;
            }
        }

        try {
            bitFlyerPublisher.publish(bitFlyerMarketDataEntry);
        } catch (MarketDataPublisherException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void processZaifRawData(ZaifRawData zaifRawData) {
        logger.trace("processing raw data.");

        // update market data entry
        if (zaifMarketDataEntry == null) {
            logger.info("creating a zaif MarketDataEntry.");
            zaifMarketDataEntry = new MarketDataEntry(
                    maxNumberOfDepths,
                    zaifRawData.getSymbol(),
                    zaifRawData.getName(),
                    zaifRawData.getMic()
            );
        }

        zaifMarketDataEntry = zaifParser.parse(zaifRawData, zaifMarketDataEntry);

        // publish market data
        if (zaifPublisher == null) {
            try {
                zaifPublisher = marketDataPublisherFactory.create(zaifMarketDataEntry.getSymbol());
            } catch (MarketDataPublisherException e) {
                logger.error(e.getMessage(), e);
                return;
            }
        }

        try {
            zaifPublisher.publish(zaifMarketDataEntry);
        } catch (MarketDataPublisherException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
