package jp.co.myogadanimotors.myogadani.store.master;

public class MasterDataInitializeException extends Exception {

    public MasterDataInitializeException() {

    }

    public MasterDataInitializeException(String message) {
        super(message);
    }

    public MasterDataInitializeException(Throwable cause) {
        super(cause);
    }

    public MasterDataInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
