package jp.co.myogadanimotors.yushima.mdmanager;

import java.math.BigDecimal;

public interface IMarketDataEntry {
    String getSymbol();

    String getName();

    String getMic();

    BigDecimal getLastPrice();

    BigDecimal getLastTradedVolume();

    BigDecimal getAskPrice(int idx);

    BigDecimal getAskSize(int idx);

    BigDecimal getBidPrice(int idx);

    BigDecimal getBidSize(int idx);
}
