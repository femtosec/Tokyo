package jp.co.myogadanimotors.kohinata.strategy.validator;

public final class RejectMessage {

    private final String message;

    public RejectMessage(String message) {
        this.message = message;
    }

    public String get() {
        return message;
    }
}
