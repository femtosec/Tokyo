package jp.co.myogadanimotors.myogadani.store.master.product;

import jp.co.myogadanimotors.myogadani.store.IStoredObject;

public interface IProduct extends IStoredObject {
    ProductType getProductType();
    String getSymbol();
    long getMarketId();
    String getDescription();
}
