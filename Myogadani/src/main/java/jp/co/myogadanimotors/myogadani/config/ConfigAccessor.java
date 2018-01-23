package jp.co.myogadanimotors.myogadani.config;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;

public class ConfigAccessor implements IConfigAccessor {

    private final Logger logger = LogManager.getLogger(getClass().getName());
    private ConfigObject configObject;

    public ConfigAccessor() {

    }

    @Override
    public void parse(String environment, String configFileName) throws FileNotFoundException {
        try {
            logger.info("parsing a config file. (configFileName: {})", configFileName);
            configObject = new ConfigSlurper(environment).parse(getClass().getClassLoader().getResource(configFileName));
        } catch (NullPointerException e) {
            throw new FileNotFoundException("cannot parse config file.");
        }
    }

    @Override
    public int getInt(String key, int defaultValue) {
        String rawValue = getRawValue(key);
        if (rawValue == null) return defaultValue;
        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        String rawValue = getRawValue(key);
        if (rawValue == null) return defaultValue;
        try {
            return Double.parseDouble(rawValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String rawValue = getRawValue(key);
        if (rawValue == null) return defaultValue;
        return Boolean.parseBoolean(getRawValue(key));
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
