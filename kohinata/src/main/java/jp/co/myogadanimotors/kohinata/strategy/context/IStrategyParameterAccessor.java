package jp.co.myogadanimotors.kohinata.strategy.context;

public interface IStrategyParameterAccessor {
    int getInt(String parameterName, int defaultValue);
    long getLong(String parameterName, long defaultValue);
    double getDouble(String parameterName, double defaultValue);
    boolean getBoolean(String parameterName, boolean defaultValue);
    String getString(String parameterName, String defaultValue);
}
