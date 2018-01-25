package jp.co.myogadanimotors.myogadani.strategy.strategyparameter

import jp.co.myogadanimotors.myogadani.common.Constants
import jp.co.myogadanimotors.myogadani.config.ConfigAccessor
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

class BaseStrategyParametersTest {

    BaseStrategyParameters baseStrategyParameters

    @BeforeClass
    void setup() {
        def config = new ConfigAccessor()
        config.parse("development", Constants.CONFIG_FILE_NAME)

        def strategyConfig = new ConfigAccessor()
        strategyConfig.parse("development", new File(config.getString("myogadani.strategyConfig.jsonFileLocation")))

        baseStrategyParameters = new BaseStrategyParameters("Peg", strategyConfig)
        baseStrategyParameters.init()
    }

    @Test
    void testBaseStrategyParametersTest() {
        assert baseStrategyParameters.getMaxNumberOfPendingAmendProcessing() == 5
        assert baseStrategyParameters.getMaxNumberOfPendingCancelProcessing() == 7
        assert baseStrategyParameters.getPendingAmendProcessingTimerInterval() == TimeUnit.MILLISECONDS.toNanos(100)
        assert baseStrategyParameters.getPendingCancelProcessingTimerInterval() == TimeUnit.MILLISECONDS.toNanos(100)
    }
}
