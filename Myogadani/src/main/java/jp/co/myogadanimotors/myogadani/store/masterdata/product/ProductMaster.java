package jp.co.myogadanimotors.myogadani.store.masterdata.product;

import jp.co.myogadanimotors.myogadani.store.masterdata.AbstractMasterDataStore;

public final class ProductMaster extends AbstractMasterDataStore<IProduct> {

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
