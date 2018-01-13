package jp.co.myogadanimotors.myogadani.timesource;

public class SystemTimeSource implements ITimeSource {
    @Override
    public long getCurrentTime() {
        return System.nanoTime();
    }
}
