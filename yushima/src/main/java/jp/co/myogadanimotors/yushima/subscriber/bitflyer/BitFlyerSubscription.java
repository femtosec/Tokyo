package jp.co.myogadanimotors.yushima.subscriber.bitflyer;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;
import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;

public class BitFlyerSubscription implements IStoredObject {

    private long id;
    private long productId;
    private String channel;
    private String type;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MarketDataType getMarketDataType() {
        return MarketDataType.valueOf(type);
    }

    @Override
    public String toString() {
        return "id: " + id
                + ", productId: " + productId
                + ", channel: " + channel
                + ", marketDataType: " + getMarketDataType();
    }
}
