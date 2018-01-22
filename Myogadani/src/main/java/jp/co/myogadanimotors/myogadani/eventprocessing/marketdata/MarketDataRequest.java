package jp.co.myogadanimotors.myogadani.eventprocessing.marketdata;

import jp.co.myogadanimotors.myogadani.eventprocessing.BaseEvent;

public class MarketDataRequest extends BaseEvent<IAsyncMarketDataRequestListener> {

    private final long requestId;
    private final long productId;
    private final String symbol;
    private final String mic;

    public MarketDataRequest(long eventId,
                             long creationTime,
                             long requestId,
                             long productId,
                             String symbol,
                             String mic,
                             IAsyncMarketDataRequestListener eventListener) {
        super(eventId, creationTime, eventListener);
        this.requestId = requestId;
        this.productId = productId;
        this.symbol = symbol;
        this.mic = mic;
    }

    public long getRequestId() {
        return requestId;
    }

    public long getProductId() {
        return productId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getMic() {
        return mic;
    }

    @Override
    protected void callEventListener(IAsyncMarketDataRequestListener eventListener) {
        eventListener.processMarketDataRequest(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", productId: ").append(productId)
                .append(", symbol: ").append(symbol)
                .append(", mic: ").append(mic);
    }
}
