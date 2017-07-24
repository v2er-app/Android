package me.ghui.v2er.network;

/**
 * Created by ghui on 24/07/2017.
 */

public class GeneralError extends Throwable {

    public GeneralError(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
