package jp.co.myogadanimotors.myogadani

import jp.co.myogadanimotors.myogadani.config.ConfigAccessor
import org.junit.Test

class ConfigTest {

    @Test
    void testConfigAccessor() {
        def config = new ConfigAccessor()
        config.parse("development", "configuration.groovy")
        assert config.getInt("myogadani.numberOfStrategyThreads", -1) == 2
        assert config.getBoolean("strategy.*.test", false)
    }
}
