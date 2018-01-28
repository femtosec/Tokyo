package jp.co.myogadanimotors.bunkyo.config;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

public class ConfigAccessor implements IConfigAccessor {

    private final Logger logger = LogManager.getLogger(getClass().getName());
    private ConfigObject configObject;

    public void parse(String environment, File configFile) throws FileNotFoundException {
        URL configUrl;
        try {
            configUrl = configFile.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("cannot parse config file.");
        }
        parse(environment, configUrl);
    }

    public void parse(String environment, URL configUrl) throws FileNotFoundException {
        try {
            logger.info("parsing a config file. (URL: {})", configUrl);
            configObject = new ConfigSlurper(environment).parse(configUrl);
        } catch (NullPointerException e) {
            throw new FileNotFoundException("cannot parse config file.");
        }
    }

    @Override
    public int getInt(String key) {
        return getInt(key, Integer.MIN_VALUE);
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
    public long getLong(String key) {
        return getLong(key, Long.MIN_VALUE);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        String rawValue = getRawValue(key);
        if (rawValue == null) return defaultValue;
        try {
            return Long.parseLong(rawValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    @Override
    public double getDouble(String key) {
        return getDouble(key, Double.NaN);
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
    public String getString(String key) {
        return getString(key, null);
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
