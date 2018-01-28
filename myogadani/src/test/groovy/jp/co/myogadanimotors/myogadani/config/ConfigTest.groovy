package jp.co.myogadanimotors.myogadani.config

import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

class ConfigTest {

    def config = new ConfigAccessor()

    @BeforeClass
    void setup() {
        config.parse("development", getClass().getClassLoader().getResource("configuration.groovy"))
    }

    @Test
    void testConfigAccessor() {
        assert config.getInt("myogadani.numberOfStrategyThreads", -1) == 1
    }
}
