package jp.co.myogadanimotors.myogadani.store.masterdata.product;

import jp.co.myogadanimotors.myogadani.store.masterdata.AbstractMasterDataStore;

public class ProductMaster extends AbstractMasterDataStore<Product> {

    @Override
    protected Product create() {
        return new Product();
    }

    public Product getBySymbol(String symbol) {
        for (Product product : objectsById.values()) {
            if (product.getSymbol() == symbol) return product;
        }

        return null;
    }
}
