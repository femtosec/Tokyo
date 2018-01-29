package jp.co.myogadanimotors.kohinata.master

import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor
import jp.co.myogadanimotors.kohinata.master.extendedattriute.ExtendedAttributeMaster
import jp.co.myogadanimotors.kohinata.master.extendedattriute.IExtendedAttributeDescriptor
import jp.co.myogadanimotors.kohinata.master.market.IMarket
import jp.co.myogadanimotors.kohinata.master.market.MarketMaster
import jp.co.myogadanimotors.kohinata.master.market.MarketType
import jp.co.myogadanimotors.kohinata.master.product.IProduct
import jp.co.myogadanimotors.kohinata.master.product.ProductMaster
import jp.co.myogadanimotors.kohinata.master.product.ProductType
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

class MasterDataTest {

    def config = new ConfigAccessor()

    @BeforeClass
    void setup() {
        config.parse("development", getClass().getClassLoader().getResource("configuration.groovy"))
    }

    @Test
    void testMarketMaster() {
        def marketMaster = new MarketMaster()
        marketMaster.init(config)

        assertMarket(marketMaster.get(0L), MarketType.CryptoCurrencyExchange, "bitFlyer", "BFL")
        assertMarket(marketMaster.get(1L), MarketType.CryptoCurrencyExchange, "Zaif", "ZAI")
    }

    private static void assertMarket(IMarket market, MarketType marketType, String name, String mic) {
        assert market.getMarketType() == marketType
        assert market.getName() == name
        assert market.getMic() == mic
    }

    @Test
    void testProductMaster() {
        def productMaster = new ProductMaster()
        productMaster.init(config)

        assertProduct(productMaster.get(0L), ProductType.CryptoCurrency, "BTC/JPY", 0L, "Bitcoin @ bitFlyer")
        assertProduct(productMaster.get(1L), ProductType.CryptoCurrency, "BTC/JPY", 1L, "Bitcoin @ Zaif")
        assertProduct(productMaster.get(2L), ProductType.CryptoCurrency, "ETH/JPY", 0L, "Ethereum @ bitFlyer")
        assertProduct(productMaster.get(3L), ProductType.CryptoCurrency, "ETH/JPY", 1L, "Ethereum @ Zaif")
    }

    private static void assertProduct(IProduct product, ProductType productType, String symbol, long marketId, String description) {
        assert product.getProductType() == productType
        assert product.getSymbol() == symbol
        assert product.getMarketId() == marketId
        assert product.getDescription() == description
    }

    // todo: move this to kohinata
//    @Test
//    void testStrategyMaster() {
//        def strategyMaster = new StrategyMaster()
//        strategyMaster.init(config)
//
//        assertStrategyDescriptor(strategyMaster.get(0L), "Peg", "This strategy places market making orders pegging at the near touch price.")
//        assertStrategyDescriptor(strategyMaster.get(1L), "Arbitrage", "Test")
//    }
//
//    private static void assertStrategyDescriptor(IStrategyDescriptor sd, String name, String description) {
//        assert sd.getName() == name
//        assert sd.getDescription() == description
//    }

    @Test
    void testExtendedAttributeMaster() {
        def extendedAttributeMaster = new ExtendedAttributeMaster()
        extendedAttributeMaster.init(config)

        assertExtendedAttribute(extendedAttributeMaster.get(0L), "strategyName", "String", "strategy name")
        assertExtendedAttribute(extendedAttributeMaster.get(1L), "symbolList", "List", "symbol list")
        assertExtendedAttribute(extendedAttributeMaster.get(2L), "micList", "List", "mic list")
        assertExtendedAttribute(extendedAttributeMaster.get(3L), "priceLimitList", "List", "price limit list")
        assertExtendedAttribute(extendedAttributeMaster.get(4L), "orderQuantityList", "List", "order quantity list")
        assertExtendedAttribute(extendedAttributeMaster.get(5L), "orderTag", "String", "order tag")
    }

    private static void assertExtendedAttribute(IExtendedAttributeDescriptor ead, String name, String type, String description) {
        assert ead.getName() == name
        assert ead.getType() == type
        assert ead.getDescription() == description
    }
}
