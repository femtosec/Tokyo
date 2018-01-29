package jp.co.myogadanimotors.kohinata.event.marketdata;

import java.math.BigDecimal;

public final class DepthEntry {

    private final BigDecimal price;
    private final BigDecimal size;
    private final boolean isUpdated;

    public DepthEntry(BigDecimal price, BigDecimal size, boolean isUpdated) {
        this.price = price;
        this.size = size;
        this.isUpdated = isUpdated;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSize() {
        return size;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    @Override
    public String toString() {
        return "[px:" + price.toString() + ", size:" + size.toString() + ", isUpdated:" + isUpdated + "]";
    }
}
