package jp.co.myogadanimotors.yushima.subscriber.zaif;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;
import jp.co.myogadanimotors.yushima.subscriber.MarketDataType;

public class ZaifSubscription implements IStoredObject {

    private long id;
    private long productId;
    private String currencyPair;
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

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
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
                + ", currencyPair: " + currencyPair
                + ", marketDataType: " + getMarketDataType();
    }
}
