package jp.co.myogadanimotors.yushima.publisher;

public class MarketDataPublisherException extends Exception {
    public MarketDataPublisherException() {

    }

    public MarketDataPublisherException(String message) {
        super(message);
    }

    public MarketDataPublisherException(Throwable cause) {
        super(cause);
    }

    public MarketDataPublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}
