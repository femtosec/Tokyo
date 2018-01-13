package jp.co.myogadanimotors.myogadani.store.masterdata.product;

import jp.co.myogadanimotors.myogadani.store.IStoredObject;

public interface IProduct extends IStoredObject {
    ProductType getProductType();
    String getSymbol();
    long getMarketId();
    String getDescription();
}
