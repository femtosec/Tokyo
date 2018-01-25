package jp.co.myogadanimotors.myogadani.config;

public interface IConfigAccessor {
    /**
     * returns config value in Integer
     * if config value doesn't exist, return Integer.MIN_VALUE
     */
    int getInt(String key);

    /**
     * returns config value in Integer
     * if config value doesn't exist, return defaultValue
     */
    int getInt(String key, int defaultValue);

    /**
     * returns config value in Long
     * if config value doesn't exist, return Long.MIN_VALUE
     */
    long getLong(String key);

    /**
     * returns config value in Long
     * if config value doesn't exist, return defaultValue
     */
    long getLong(String key, long defaultValue);

    /**
     * returns config value in Double
     * if config value doesn't exist, return Double.NaN
     */
    double getDouble(String key);

    /**
     * returns config value in Double
     * if config value doesn't exist, return defaultValue
     */
    double getDouble(String key, double defaultValue);

    /**
     * returns config value in Boolean
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * returns config value in String
     * if config value doesn't exist, return null
     */
    String  getString(String key);

    /**
     * returns config value in String
     * if config value doesn't exist, return defaultValue
     */
    String  getString(String key, String defaultValue);
}
