package jp.co.myogadanimotors.kohinata.strategy.context;

import jp.co.myogadanimotors.bunkyo.config.IConfigAccessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class StrategyParameterAccessor implements IStrategyParameterAccessor {

    private final String strategyName;
    private final IConfigAccessor strategyConfigAccessor;
    private final Map<String, String> extendedAttributes = new ConcurrentHashMap<>();

    public StrategyParameterAccessor(String strategyName, IConfigAccessor strategyConfigAccessor) {
        this.strategyName = requireNonNull(strategyName);
        this.strategyConfigAccessor = requireNonNull(strategyConfigAccessor);
    }

    public void updateExtendedAttributes(Map<String, String> extendedAttributes) {
        this.extendedAttributes.clear();
        this.extendedAttributes.putAll(extendedAttributes);
    }

    @Override
    public int getInt(String parameterName, int defaultValue) {
        String rawValue = extendedAttributes.get(parameterName);
        if (rawValue != null) {
            try {
                return Integer.parseInt(rawValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return getConfigInInt(parameterName, defaultValue);
    }

    @Override
    public long getLong(String parameterName, long defaultValue) {
        String rawValue = extendedAttributes.get(parameterName);
        if (rawValue != null) {
            try {
                return Long.parseLong(rawValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return getConfigInLong(parameterName, defaultValue);
    }

    @Override
    public double getDouble(String parameterName, double defaultValue) {
        String rawValue = extendedAttributes.get(parameterName);
        if (rawValue != null) {
            try {
                return Double.parseDouble(rawValue);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return getConfigInDouble(parameterName, defaultValue);
    }

    @Override
    public boolean getBoolean(String parameterName, boolean defaultValue) {
        String rawValue = extendedAttributes.get(parameterName);
        if (rawValue != null) {
            return Boolean.parseBoolean(rawValue);
        }
        return getConfigInBoolean(parameterName, defaultValue);
    }

    @Override
    public String getString(String parameterName, String defaultValue) {
        String val = extendedAttributes.get(parameterName);
        if (val != null) return val;
        return getConfigInString(parameterName, defaultValue);
    }

    private int getConfigInInt(String parameterName, int defaultValue) {
        return strategyConfigAccessor.getInt(
                getStrategyConfigSpecificKey(parameterName),
                strategyConfigAccessor.getInt(getConfigDefaultKey(parameterName), defaultValue)
        );
    }

    private long getConfigInLong(String parameterName, long defaultValue) {
        return strategyConfigAccessor.getLong(
                getStrategyConfigSpecificKey(parameterName),
                strategyConfigAccessor.getLong(getConfigDefaultKey(parameterName), defaultValue)
        );
    }

    private double getConfigInDouble(String parameterName, double defaultValue) {
        return strategyConfigAccessor.getDouble(
                getStrategyConfigSpecificKey(parameterName),
                strategyConfigAccessor.getDouble(getConfigDefaultKey(parameterName), defaultValue)
        );
    }

    private boolean getConfigInBoolean(String parameterName, boolean defaultValue) {
        return strategyConfigAccessor.getBoolean(
                getStrategyConfigSpecificKey(parameterName),
                strategyConfigAccessor.getBoolean(getConfigDefaultKey(parameterName), defaultValue)
        );
    }

    private String getConfigInString(String parameterName, String defaultValue) {
        return strategyConfigAccessor.getString(
                getStrategyConfigSpecificKey(parameterName),
                strategyConfigAccessor.getString(getConfigDefaultKey(parameterName), defaultValue)
        );
    }

    private String getConfigDefaultKey(String parameterName) {
        return "strategy.*." + parameterName;
    }

    private String getStrategyConfigSpecificKey(String parameterName) {
        return "strategy." + strategyName + "." + parameterName;
    }
}
