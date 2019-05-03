package me.ghui.v2er.network;

import me.ghui.toolbox.android.Check;

/**
 * Created by ghui on 24/07/2017.
 */

public class GeneralError extends Throwable {

    private String response;
    private String message;
    private int errorCode;

    public GeneralError(int errorCode, String message) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        if (Check.notEmpty(message)) return message;
        return super.getMessage();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
