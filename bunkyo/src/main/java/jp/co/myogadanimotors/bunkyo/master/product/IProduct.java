package jp.co.myogadanimotors.bunkyo.master.product;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;

public interface IProduct extends IStoredObject {
    ProductType getProductType();
    String getSymbol();
    long getMarketId();
    String getDescription();
}
