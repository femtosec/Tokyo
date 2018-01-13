package jp.co.myogadanimotors.myogadani.config;

public interface IConfigAccessor {
    /**
     * initializes IConfigAccessor
     */
    void parse(String environment, String configFileName) throws Exception;

    /**
     * returns config value in Integer
     * @param key
     * @param defaultValue
     * @return
     */
    int getInt(String key, int defaultValue);

    /**
     * returns config value in Double
     * @param key
     * @param defaultValue
     * @return
     */
    double getDouble(String key, double defaultValue);

    /**
     * returns config value in Boolean
     * @param key
     * @param defaultValue
     * @return
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * returns config value in String
     * @param key
     * @param defaultValue
     * @return
     */
    String  getString(String key, String defaultValue);
}
