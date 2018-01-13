package jp.co.myogadanimotors.myogadani.store.masterdata

import jp.co.myogadanimotors.myogadani.common.Constants
import jp.co.myogadanimotors.myogadani.config.ConfigAccessor
import jp.co.myogadanimotors.myogadani.store.masterdata.market.IMarket
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketMaster
import jp.co.myogadanimotors.myogadani.store.masterdata.market.MarketType
import jp.co.myogadanimotors.myogadani.store.masterdata.product.IProduct
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductMaster
import jp.co.myogadanimotors.myogadani.store.masterdata.product.ProductType
import org.junit.Test

class MasterDataTest {

    @Test
    void testMarketMaster() {
        def config = new ConfigAccessor()
        config.parse("development", Constants.CONFIG_FILE_NAME)

        def marketMaster = new MarketMaster()
        marketMaster.init(config)

        assertMarket(marketMaster.get(0L), MarketType.CryptoCurrencyExchange, "bitFlyer", "BFL")
        assertMarket(marketMaster.get(1L), MarketType.CryptoCurrencyExchange, "Coincheck", "CCH")
        assertMarket(marketMaster.get(2L), MarketType.CryptoCurrencyExchange, "Zaif", "ZAI")
    }

    private void assertMarket(IMarket market, MarketType marketType, String name, String mic) {
        assert market.getMarketType() == marketType
        assert market.getName() == name
        assert market.getMic() == mic
    }

    @Test
    void testProductMaster() {
        def config = new ConfigAccessor()
        config.parse("development", Constants.CONFIG_FILE_NAME)

        def productMaster = new ProductMaster()
        productMaster.init(config)

        assertProduct(productMaster.get(0L), ProductType.CryptoCurrency, "BTC/JPY", 0L, "Bitcoin @ bitFlyer")
        assertProduct(productMaster.get(1L), ProductType.CryptoCurrency, "BTC/JPY", 1L, "Bitcoin @ Coincheck")
        assertProduct(productMaster.get(2L), ProductType.CryptoCurrency, "BTC/JPY", 2L, "Bitcoin @ Zaif")
        assertProduct(productMaster.get(3L), ProductType.CryptoCurrency, "ETH/JPY", 0L, "Ethereum @ bitFlyer")
        assertProduct(productMaster.get(4L), ProductType.CryptoCurrency, "ETH/JPY", 1L, "Ethereum @ Coincheck")
        assertProduct(productMaster.get(5L), ProductType.CryptoCurrency, "ETH/JPY", 2L, "Ethereum @ Zaif")
    }

    private void assertProduct(IProduct product, ProductType productType, String symbol, long marketId, String description) {
        assert product.getProductType() == productType
        assert product.getSymbol() == symbol
        assert product.getMarketId() == marketId
        assert product.getDescription() == description
    }
}
