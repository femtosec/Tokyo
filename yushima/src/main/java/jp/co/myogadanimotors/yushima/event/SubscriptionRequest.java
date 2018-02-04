package jp.co.myogadanimotors.yushima.event;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

public final class SubscriptionRequest extends BaseEvent<IAsyncSubscriptionRequestListener> {

    private final long requestId;
    private final long productId;
    private final String symbol;
    private final String mic;

    public SubscriptionRequest(long eventId,
                               long creationTime,
                               long requestId,
                               long productId,
                               String symbol,
                               String mic,
                               IAsyncSubscriptionRequestListener eventListener) {
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
    protected void callEventListener(IAsyncSubscriptionRequestListener eventListener) {
        eventListener.processSubscriptionRequest(this);
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
