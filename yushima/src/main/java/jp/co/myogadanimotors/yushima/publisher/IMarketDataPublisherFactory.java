package jp.co.myogadanimotors.yushima.publisher;

public interface IMarketDataPublisherFactory {
    IMarketDataPublisher create(String symbol) throws MarketDataPublisherException;
}
