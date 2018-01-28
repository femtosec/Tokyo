package jp.co.myogadanimotors.myogadani.strategy.validator;

public final class RejectMessage {

    private final String message;

    public RejectMessage(String message) {
        this.message = message;
    }

    public String get() {
        return message;
    }
}
