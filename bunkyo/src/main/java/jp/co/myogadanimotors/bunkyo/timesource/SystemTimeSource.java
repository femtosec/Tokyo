package jp.co.myogadanimotors.bunkyo.timesource;

public class SystemTimeSource implements ITimeSource {
    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
