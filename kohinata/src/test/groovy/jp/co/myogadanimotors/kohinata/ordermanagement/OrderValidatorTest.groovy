package jp.co.myogadanimotors.kohinata.ordermanagement

import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor
import jp.co.myogadanimotors.bunkyo.config.IConfigAccessor
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator
import jp.co.myogadanimotors.bunkyo.master.market.IMarket
import jp.co.myogadanimotors.bunkyo.master.market.MarketMaster
import jp.co.myogadanimotors.bunkyo.master.market.MarketState
import jp.co.myogadanimotors.bunkyo.master.market.MarketType
import jp.co.myogadanimotors.bunkyo.master.product.IProduct
import jp.co.myogadanimotors.bunkyo.master.product.ProductMaster
import jp.co.myogadanimotors.bunkyo.timesource.ITimeSource
import jp.co.myogadanimotors.kohinata.event.RequestIdGenerator
import jp.co.myogadanimotors.kohinata.event.marketdata.IAsyncMarketDataRequestListener
import jp.co.myogadanimotors.kohinata.event.order.*
import jp.co.myogadanimotors.kohinata.event.report.IAsyncFillListener
import jp.co.myogadanimotors.kohinata.event.report.IAsyncReportListener
import jp.co.myogadanimotors.kohinata.event.timer.IAsyncTimerRegistrationListener
import jp.co.myogadanimotors.kohinata.master.extendedattriute.ExtendedAttributeMaster
import jp.co.myogadanimotors.kohinata.master.strategy.IStrategyDescriptor
import jp.co.myogadanimotors.kohinata.ordermanagement.order.IOrder
import jp.co.myogadanimotors.kohinata.ordermanagement.order.Order
import jp.co.myogadanimotors.kohinata.ordermanagement.order.OrderState
import jp.co.myogadanimotors.kohinata.strategy.IStrategy
import jp.co.myogadanimotors.kohinata.strategy.context.StrategyContext
import jp.co.myogadanimotors.kohinata.strategy.validator.IValidator
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.util.concurrent.ConcurrentHashMap

class OrderValidatorTest {

    OrderValidator orderValidator

    @BeforeClass
    void setup() {
        def configAccessor = new ConfigAccessor()
        def marketMaster = new MarketMaster()
        def productMaster = new ProductMaster()
        def extendedAttributeMaster = new ExtendedAttributeMaster()
        configAccessor.parse("development", getClass().getClassLoader().getResource("configuration.groovy"))
        marketMaster.init(configAccessor)
        productMaster.init(configAccessor)
        extendedAttributeMaster.init(configAccessor)

        orderValidator = new OrderValidator(marketMaster, productMaster, extendedAttributeMaster)
    }

    private static StrategyContext createStrategyContext(IOrder order) {
        return new StrategyContext(
                new EventIdGenerator(),
                new RequestIdGenerator(),
                [
                        getCurrentTime: { -> 0L }
                ] as ITimeSource,
                order,
                [
                        getId: { -> 0L },
                        getMarketType: { -> MarketType.CryptoCurrencyExchange },
                        getName: { -> "" },
                        getMic: { -> "" },
                        getMarketState: { -> MarketState.Trading }
                ] as IMarket,
                [ ] as IProduct,
                [
                        getName: { -> "" }
                ] as IStrategyDescriptor,
                [
                        getValidatorList: { -> new ArrayList<IValidator>() }
                ] as IStrategy,
                [ ] as IConfigAccessor,
                [ ] as IAsyncOrderListener,
                [ ] as IAsyncReportListener,
                [ ] as IAsyncFillListener,
                [ ] as IAsyncMarketDataRequestListener,
                [ ] as IAsyncTimerRegistrationListener
        )
    }

    @DataProvider(name = "isValidNewOrder")
    Object[][] createTestCasesForIsValidNewOrder() {
        [
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Buy,      Orderer.Strategy,   OrderDestination.Exchange,      0L,     "symbolList",   false,  true ],
                [ "XXX/XXX.BFL",    "BFL",  OrderSide.Buy,      Orderer.Strategy,   OrderDestination.Exchange,      0L,     "symbolList",   false,  false ],
                [ "BTC/JPY.ZAI",    "ZAI",  OrderSide.Buy,      Orderer.Strategy,   OrderDestination.Exchange,      0L,     "symbolList",   false,  true ],
                [ "BTC/JPY.XXX",    "XXX",  OrderSide.Buy,      Orderer.Strategy,   OrderDestination.Exchange,      0L,     "symbolList",   false,  false ],
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Sell,     Orderer.Strategy,   OrderDestination.Exchange,      0L,     "symbolList",   false,  true ],
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Both,     Orderer.Strategy,   OrderDestination.Exchange,      0L,     "symbolList",   false,  true ],
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Buy,      Orderer.EMS,        OrderDestination.Exchange,      0L,     "symbolList",   false,  true ],
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Buy,      Orderer.EMS,        OrderDestination.Exchange,      0L,     "symbolList",   true,   false ],
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Buy,      Orderer.EMS,        OrderDestination.Strategy,      0L,     "symbolList",   true,   true ],
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Buy,      Orderer.EMS,        OrderDestination.Strategy,      0L,     "symbolList",   false,  false ],
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Buy,      Orderer.Strategy,   OrderDestination.Exchange,      -1L,    "symbolList",   false,  false ],
                [ "BTC/JPY.BFL",    "BFL",  OrderSide.Buy,      Orderer.Strategy,   OrderDestination.Exchange,      0L,     "_symbolList",  false,  false ],
        ]
    }

    @Test(dataProvider = "isValidNewOrder")
    void testIsValidNewOrder(String symbol,
                             String mic,
                             OrderSide orderSide,
                             Orderer orderer,
                             OrderDestination destination,
                             long requestId,
                             String extendedAttributeKey,
                             boolean shouldSetStrategyContext,
                             boolean expectedResult) {
        def extendedAttributes = new ConcurrentHashMap<String, String>()
        extendedAttributes.put(extendedAttributeKey, "")
        def newOrderEvent = new NewOrder(
                0L,
                0L,
                [ ] as IAsyncOrderListener,
                requestId,
                0L,
                0L,
                symbol,
                mic,
                orderSide,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                orderer,
                destination,
                extendedAttributes
        )

        def newOrder = new Order(
                0L,
                0L,
                symbol,
                mic,
                "",
                orderSide,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                orderer,
                destination,
                extendedAttributes,
                null
        )

        if (shouldSetStrategyContext) {
            newOrder.setStrategyContext(createStrategyContext(newOrder))
        }

        assert orderValidator.isValidNewOrder(newOrderEvent, newOrder) == expectedResult
    }


    @DataProvider(name = "isValidAmendOrder")
    Object[][] createTestCasesForIsValidAmendOrder() {
        [
                [ OrderState.New,               0L,     "symbolList",   false ],
                [ OrderState.NewAck,            0L,     "symbolList",   true ],
                [ OrderState.NewReject,         0L,     "symbolList",   false ],
                [ OrderState.Amend,             0L,     "symbolList",   false ],
                [ OrderState.AmendAck,          0L,     "symbolList",   true ],
                [ OrderState.AmendReject,       0L,     "symbolList",   true ],
                [ OrderState.Cancel,            0L,     "symbolList",   false ],
                [ OrderState.CancelAck,         0L,     "symbolList",   false ],
                [ OrderState.CancelReject,      0L,     "symbolList",   true ],
                [ OrderState.UnsolicitedCancel, 0L,     "symbolList",   false ],
                [ OrderState.FullyFilled,       0L,     "symbolList",   false ],
                [ OrderState.NewAck,            -1L,    "symbolList",   false ],
                [ OrderState.NewAck,            0L,     "_symbolList",  false ],
        ]
    }

    @Test(dataProvider = "isValidAmendOrder")
    void testIsValidAmendOrder(OrderState orderState,
                               long requestId,
                               String extendedAttributeKey,
                               boolean expectedResult) {
        def extendedAttributes = new ConcurrentHashMap<String, String>()
        extendedAttributes.put(extendedAttributeKey, "")
        def amendOrderEvent = new AmendOrder(
                0L,
                0L,
                [ ] as IAsyncOrderListener,
                requestId,
                0L,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                extendedAttributes
        )

        def currentOrder = new Order(
                0L,
                0L,
                "BTC/JPY",
                "BFL",
                "",
                OrderSide.Buy,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Orderer.EMS,
                OrderDestination.Strategy,
                extendedAttributes,
                null
        )
        currentOrder.setOrderState(orderState)
        assert orderValidator.isValidAmendOrder(amendOrderEvent, currentOrder) == expectedResult
    }

    @DataProvider(name = "isValidCancelOrder")
    Object[][] createTestCasesForIsValidCancelOrder() {
        [
                [ OrderState.New,               0L,     false ],
                [ OrderState.NewAck,            0L,     true ],
                [ OrderState.NewReject,         0L,     false ],
                [ OrderState.Amend,             0L,     false ],
                [ OrderState.AmendAck,          0L,     true ],
                [ OrderState.AmendReject,       0L,     true ],
                [ OrderState.Cancel,            0L,     false ],
                [ OrderState.CancelAck,         0L,     false ],
                [ OrderState.CancelReject,      0L,     true ],
                [ OrderState.UnsolicitedCancel, 0L,     false ],
                [ OrderState.FullyFilled,       0L,     false ],
                [ OrderState.NewAck,            -1L,    false ],
        ]
    }

    @Test(dataProvider = "isValidCancelOrder")
    void testIsValidCancelOrder(OrderState orderState,
                               long requestId,
                               boolean expectedResult) {
        def extendedAttributes = new ConcurrentHashMap<String, String>()
        def cancelOrderEvent = new CancelOrder(
                0L,
                0L,
                [ ] as IAsyncOrderListener,
                requestId,
                0L
        )

        def currentOrder = new Order(
                0L,
                0L,
                "BTC/JPY",
                "BFL",
                "",
                OrderSide.Buy,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Orderer.EMS,
                OrderDestination.Strategy,
                extendedAttributes,
                null
        )
        currentOrder.setOrderState(orderState)
        assert orderValidator.isValidCancelOrder(cancelOrderEvent, currentOrder) == expectedResult
    }
}
