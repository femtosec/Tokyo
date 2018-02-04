package jp.co.myogadanimotors.yushima.publisher;

import jp.co.myogadanimotors.yushima.mdmanager.IMarketDataEntry;

public interface IMarketDataPublisher {
    void publish(IMarketDataEntry marketDataEntry) throws MarketDataPublisherException;
    void close() throws MarketDataPublisherException;
}
