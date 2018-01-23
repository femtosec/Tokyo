package jp.co.myogadanimotors.myogadani.store.masterdata.product;

import jp.co.myogadanimotors.myogadani.common.Constants;

public final class Product implements IProduct {

    private long id = Constants.NOT_SET_ID_LONG;
    private ProductType productType;
    private String symbol;
    private long marketId = Constants.NOT_SET_ID_LONG;
    private String description;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductTypeString() {
        return productType.toString();
    }

    public void setProductTypeString(String productTypeString) {
        productType = ProductType.valueOf(productTypeString);
    }

    @Override
    public ProductType getProductType() {
        return productType;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public long getMarketId() {
        return marketId;
    }

    public void setMarketId(long marketId) {
        this.marketId = marketId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("productId: ").append(id)
                .append(", productType: ").append(productType)
                .append(", symbol: ").append(symbol)
                .append(", marketId: ").append(marketId)
                .append(" description: ").append(description)
                .toString();
    }
}
