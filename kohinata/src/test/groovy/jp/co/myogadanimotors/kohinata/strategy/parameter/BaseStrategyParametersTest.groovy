package jp.co.myogadanimotors.kohinata.strategy.parameter

import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

class BaseStrategyParametersTest {

    BaseStrategyParameters baseStrategyParameters

    @BeforeClass
    void setup() {
        def strategyConfig = new ConfigAccessor()
        strategyConfig.parse("development", getClass().getClassLoader().getResource("strategy_config.groovy"))

        baseStrategyParameters = new BaseStrategyParameters("Peg", strategyConfig)
        baseStrategyParameters.init()
    }

    @Test
    void testBaseStrategyParametersTest() {
        assert baseStrategyParameters.getMaxNumberOfPendingAmendProcessing() == 5
        assert baseStrategyParameters.getMaxNumberOfPendingCancelProcessing() == 7
        assert baseStrategyParameters.getPendingAmendProcessingTimerInterval() == 100
        assert baseStrategyParameters.getPendingCancelProcessingTimerInterval() == 100
    }
}
