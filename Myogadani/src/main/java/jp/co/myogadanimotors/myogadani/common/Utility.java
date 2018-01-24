package jp.co.myogadanimotors.myogadani.common;

public class Utility {

    public static <U> U notNull(U value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }

}
