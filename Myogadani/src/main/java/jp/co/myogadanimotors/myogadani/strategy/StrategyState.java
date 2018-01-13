package jp.co.myogadanimotors.myogadani.strategy;

public enum StrategyState {
    Working(false),
    Rejected(true),
    FullyFilled(true),
    Cancelled(true),
    UnsolicitedCancel(true),
    PendingNew(false),
    PendingAmend(false),
    PendingCancel(false);

    private final boolean isTerminalState;

    StrategyState(boolean isTerminalState) {
        this.isTerminalState = isTerminalState;
    }

    public final boolean isTerminalState() {
        return isTerminalState;
    }
}
