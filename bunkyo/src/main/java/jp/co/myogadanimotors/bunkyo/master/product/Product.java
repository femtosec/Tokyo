package jp.co.myogadanimotors.bunkyo.master.product;

public final class Product implements IProduct {

    private long id = Long.MIN_VALUE;
    private ProductType productType;
    private String symbol;
    private String name;
    private long marketId = Long.MIN_VALUE;
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "productId: " + id +
                ", productType: " + productType +
                ", symbol: " + symbol +
                ", name: " + name +
                ", marketId: " + marketId +
                ", description: " + description;
    }
}
