package jp.co.myogadanimotors.myogadani.config;

import java.io.FileNotFoundException;

public interface IConfigAccessor {
    /**
     * initializes IConfigAccessor
     */
    void parse(String environment, String configFileName) throws FileNotFoundException;

    /**
     * returns config value in Integer
     */
    int getInt(String key, int defaultValue);

    /**
     * returns config value in Double
     */
    double getDouble(String key, double defaultValue);

    /**
     * returns config value in Boolean
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * returns config value in String
     */
    String  getString(String key, String defaultValue);
}
