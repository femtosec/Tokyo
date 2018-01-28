package jp.co.myogadanimotors.myogadani.master.product;

import jp.co.myogadanimotors.bunkyo.master.AbstractMaster;

public final class ProductMaster extends AbstractMaster<IProduct> {

    @Override
    protected IProduct create() {
        return new Product();
    }

    public IProduct getBySymbol(String symbol) {
        return get(product -> product.getSymbol().equals(symbol));
    }
}
