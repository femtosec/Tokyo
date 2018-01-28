package jp.co.myogadanimotors.myogadani.ordermanagement.order;

public enum OrderState {
    New(false, false, false),
    NewAck(false, true, true),
    NewReject(true, false, false),
    Amend(false, true, false),
    AmendAck(false, true, true),
    AmendReject(false, true, true),
    Cancel(false, true, false),
    CancelAck(true, false, false),
    CancelReject(false, true, true),
    UnsolicitedCancel(true, false, false),
    FullyFilled(true, false, false);

    private final boolean isTerminalState;
    private final boolean isWorking;
    private final boolean isAmendable;

    OrderState(boolean isTerminalState, boolean isWorking, boolean isAmendable) {
        this.isTerminalState = isTerminalState;
        this.isWorking = isWorking;
        this.isAmendable = isAmendable;
    }

    public boolean isTerminalState() {
        return isTerminalState;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public boolean isAmendable() {
        return isAmendable;
    }

    public boolean isCancellable() {
        return isAmendable();
    }
}
