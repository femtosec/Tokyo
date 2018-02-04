package jp.co.myogadanimotors.bunkyo.master.product;

import jp.co.myogadanimotors.bunkyo.master.IStoredObject;

public interface IProduct extends IStoredObject {
    ProductType getProductType();
    String getSymbol();
    String getName();
    long getMarketId();
    String getDescription();
}
