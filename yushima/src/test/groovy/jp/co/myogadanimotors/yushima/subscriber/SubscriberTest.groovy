package jp.co.myogadanimotors.yushima.subscriber

import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor
import jp.co.myogadanimotors.bunkyo.eventprocessing.EventIdGenerator
import jp.co.myogadanimotors.bunkyo.master.market.MarketMaster
import jp.co.myogadanimotors.bunkyo.master.product.ProductMaster
import jp.co.myogadanimotors.bunkyo.timesource.SystemTimeSource
import jp.co.myogadanimotors.yushima.event.IAsyncRawDataListener
import jp.co.myogadanimotors.yushima.subscriber.bitflyer.BitFlyerSubscriber
import jp.co.myogadanimotors.yushima.subscriber.zaif.ZaifSubscriber
import org.junit.After
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SubscriberTest {

    ConfigAccessor config = new ConfigAccessor()
    ExecutorService eventQueue = Executors.newSingleThreadExecutor()
    IAsyncRawDataListener listener = new ListenerTest(eventQueue)
    MarketMaster marketMaster = new MarketMaster()
    ProductMaster productMaster = new ProductMaster()

    @BeforeClass
    void setup() {
        config.parse("development", getClass().getClassLoader().getResource("configuration.groovy"))
        marketMaster.init(config)
        productMaster.init(config)
    }

    @After
    void finalize() {
        eventQueue.shutdown()
        eventQueue.awaitTermination(1000L, TimeUnit.MILLISECONDS)
    }

    @Test
    void testBitFlyer() {
        def bfs = new BitFlyerSubscriber(new EventIdGenerator(), new SystemTimeSource(), marketMaster, productMaster)
        bfs.addEventListener(listener)
        bfs.init(config)
        bfs.connect()

        sleep(5000L)

        bfs.disconnect()
    }

    @Test
    void testZaif() {
        def zf = new ZaifSubscriber(new EventIdGenerator(), new SystemTimeSource(), marketMaster, productMaster)
        zf.addEventListener(listener)
        zf.init(config)
        zf.connect()

        sleep(5000L)

        zf.disconnect()
    }
}
