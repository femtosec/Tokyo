package jp.co.myogadanimotors.kohinata.event.marketdata;

import jp.co.myogadanimotors.bunkyo.eventprocessing.BaseEvent;

import java.math.BigDecimal;

public final class MarketData extends BaseEvent<IAsyncMarketDataListener> {

    private final long requestId;
    private final long productId;
    private final String symbol;
    private final String name;
    private final String mic;
    private final BigDecimal lastPrice;
    private final BigDecimal lastTradeVolume;
    private final DepthEntry[] bidDepth;
    private final DepthEntry[] offerDepth;

    public MarketData(long eventId,
                      long creationTime,
                      IAsyncMarketDataListener eventListener,
                      long requestId,
                      long productId,
                      String symbol,
                      String name,
                      String mic,
                      BigDecimal lastPrice,
                      BigDecimal lastTradeVolume,
                      DepthEntry[] bidDepth,
                      DepthEntry[] offerDepth) {
        super(eventId, creationTime, eventListener);
        this.requestId = requestId;
        this.productId = productId;
        this.symbol = symbol;
        this.name = name;
        this.mic = mic;
        this.lastPrice = lastPrice;
        this.lastTradeVolume = lastTradeVolume;
        this.bidDepth = bidDepth;
        this.offerDepth = offerDepth;
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

    public String getName() {
        return name;
    }

    public String getMic() {
        return mic;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public BigDecimal getLastTradeVolume() {
        return lastTradeVolume;
    }

    public DepthEntry[] getBidDepth() {
        return bidDepth;
    }

    public DepthEntry[] getOfferDepth() {
        return offerDepth;
    }

    @Override
    protected void callEventListener(IAsyncMarketDataListener eventListener) {
        eventListener.processMarketData(this);
    }

    @Override
    public StringBuilder toStringBuilder() {
        StringBuilder sb = super.toStringBuilder()
                .append(", requestId: ").append(requestId)
                .append(", productId: ").append(productId)
                .append(", symbol: ").append(symbol)
                .append(", name: ").append(name)
                .append(", mic: ").append(mic)
                .append(", lastPrice: ").append(lastPrice)
                .append(", lastTradeVolume: ").append(lastTradeVolume)
                .append(", offer: ");

        for (DepthEntry depthEntry : offerDepth) {
            sb.append(depthEntry.toString());
        }

        sb.append(", bid: ");

        for (DepthEntry depthEntry : bidDepth) {
            sb.append(depthEntry.toString());
        }

        return sb;
    }
}
