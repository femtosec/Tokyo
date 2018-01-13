package jp.co.myogadanimotors.myogadani.config;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigAccessor implements IConfigAccessor {

    private final Logger logger = LogManager.getLogger(getClass().getName());
    private ConfigObject configObject;

    public ConfigAccessor() {

    }

    @Override
    public void parse(String environment, String configFileName) throws Exception {
        try {
            logger.info("parsing a config file. (configFileName: {})", configFileName);
            configObject = new ConfigSlurper(environment).parse(getClass().getClassLoader().getResource(configFileName));
        } catch (Exception e) {
            throw new Exception("cannot parse config file.", e);
        }
    }

    @Override
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getRawValue(key));
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(getRawValue(key));
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(getRawValue(key));
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public String getString(String key, String defaultValue) {
        String val = getRawValue(key);
        if (val != null) return val;
        return defaultValue;
    }

    private String getRawValue(String key) {
        if (configObject == null) {
            logger.warn("ConfigAccessor is not initialized.");
            return null;
        }
        return configObject.toProperties().getProperty(key);
    }
}
