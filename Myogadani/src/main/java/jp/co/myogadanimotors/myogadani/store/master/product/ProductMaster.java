package jp.co.myogadanimotors.myogadani.store.master.product;

import jp.co.myogadanimotors.myogadani.store.master.AbstractDataMaster;

public final class ProductMaster extends AbstractDataMaster<IProduct> {

    @Override
    protected IProduct create() {
        return new Product();
    }

    public IProduct getBySymbol(String symbol) {
        for (IProduct product : objectsById.values()) {
            if (product.getSymbol().equals(symbol)) return product;
        }

        return null;
    }
}
