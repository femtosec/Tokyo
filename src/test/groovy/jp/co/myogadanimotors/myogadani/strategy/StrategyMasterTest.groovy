package jp.co.myogadanimotors.myogadani.strategy

import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor
import jp.co.myogadanimotors.kohinata.master.strategy.IStrategyDescriptor
import jp.co.myogadanimotors.kohinata.master.strategy.StrategyMaster
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

class StrategyMasterTest {

    def config = new ConfigAccessor()

    @BeforeClass
    void setup() {
        config.parse("development", getClass().getClassLoader().getResource("strategy_config.groovy"))
    }

    @Test
    void testStrategyMaster() {
        def strategyMaster = new StrategyMaster()
        strategyMaster.init(config)

        assertStrategyDescriptor(strategyMaster.get(0L), "Peg", "The strategy places market making orders pegging at the near touch price.")
        assertStrategyDescriptor(strategyMaster.get(1L), "Arbitrage", "Test")
    }

    private static void assertStrategyDescriptor(IStrategyDescriptor sd, String name, String description) {
        assert sd.getName() == name
        assert sd.getDescription() == description
    }
}
