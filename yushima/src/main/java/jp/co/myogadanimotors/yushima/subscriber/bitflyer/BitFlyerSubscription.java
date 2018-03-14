package jp.co.myogadanimotors.yushima.subscriber.bitflyer;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BitFlyerSubscription implements IStoredObject {

    private long id;
    private long productId;
    private Map<String, String> channels = new ConcurrentHashMap<>();

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

    public Map<String, String> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, String> channels) {
        this.channels.putAll(channels);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
                .append("id: ").append(id)
                .append(", productId: ").append(productId)
                .append(", channels: ");

        for (Map.Entry<String, String> entry: channels.entrySet()) {
            sb.append("[").append(entry.getKey())
                    .append(",").append(entry.getValue())
                    .append("]");
        }

        return sb.toString();
    }
}
