package jp.co.myogadanimotors.yushima.mdmanager;

import java.math.BigDecimal;

public class MarketDataEntry implements IMarketDataEntry {

    private final int maxNumberOfDepths;
    private final String symbol;
    private final String name;
    private final String mic;

    private BigDecimal lastPrice;
    private BigDecimal lastTradedVolume = BigDecimal.ZERO;
    private DepthEntry[] asks;
    private DepthEntry[] bids;

    public MarketDataEntry(int maxNumberOfDepths, String symbol, String name, String mic) {
        this.maxNumberOfDepths = maxNumberOfDepths;
        this.symbol = symbol;
        this.name = name;
        this.mic = mic;

        asks = new DepthEntry[maxNumberOfDepths];
        bids = new DepthEntry[maxNumberOfDepths];
        for (int i = 0; i < maxNumberOfDepths; i++) {
            asks[i] = new DepthEntry(i);
            bids[i] = new DepthEntry(i);
        }
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMic() {
        return mic;
    }

    @Override
    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    @Override
    public BigDecimal getLastTradedVolume() {
        return lastTradedVolume;
    }

    public void setLastTradedVolume(BigDecimal lastTradedVolume) {
        this.lastTradedVolume = lastTradedVolume;
    }

    @Override
    public BigDecimal getAskPrice(int idx) {
        if (maxNumberOfDepths <= idx) return null;
        return asks[idx].price;
    }

    @Override
    public BigDecimal getAskSize(int idx) {
        if (maxNumberOfDepths <= idx) return BigDecimal.ZERO;
        return asks[idx].size;
    }

    public void setAsk(int idx, BigDecimal price, BigDecimal size) {
        if (maxNumberOfDepths <= idx) return;
        asks[idx].price = price;
        asks[idx].size = size;
    }

    @Override
    public BigDecimal getBidPrice(int idx) {
        if (maxNumberOfDepths <= idx) return null;
        return bids[idx].price;
    }

    @Override
    public BigDecimal getBidSize(int idx) {
        if (maxNumberOfDepths <= idx) return BigDecimal.ZERO;
        return bids[idx].size;
    }

    public void setBid(int idx, BigDecimal price, BigDecimal size) {
        if (maxNumberOfDepths <= idx) return;
        bids[idx].price = price;
        bids[idx].size = size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("symbol: ").append(symbol)
                .append(", name: ").append(name)
                .append(", mic: ").append(mic)
                .append(", lastPrice: ").append(lastPrice)
                .append(", lastTradedVolume: ").append(lastTradedVolume)
                .append(", asks: ");

        for (DepthEntry depthEntry: asks) {
            if (depthEntry.price == null) {
                sb.append("[").append(depthEntry.idx)
                        .append(",").append(depthEntry.price)
                        .append(",").append(depthEntry.size)
                        .append("]");
            }
        }

        sb.append(", bids: ");

        for (DepthEntry depthEntry: bids) {
            if (depthEntry.price == null) {
                sb.append("[").append(depthEntry.idx)
                        .append(",").append(depthEntry.price)
                        .append(",").append(depthEntry.size)
                        .append("]");
            }
        }

        return sb.toString();
    }

    private class DepthEntry {
        private final int idx;
        private BigDecimal price;
        private BigDecimal size = BigDecimal.ZERO;

        private DepthEntry(int idx) {
            this.idx = idx;
        }

        private void clear() {
            price = null;
            size = BigDecimal.ZERO;
        }
    }
}
