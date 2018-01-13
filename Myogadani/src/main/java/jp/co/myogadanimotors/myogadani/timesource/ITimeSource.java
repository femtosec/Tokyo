package jp.co.myogadanimotors.myogadani.timesource;

public interface ITimeSource {
    /**
     * returns current time in nano second
     */
    long getCurrentTime();
}
