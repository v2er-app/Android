package me.ghui.v2er.network;

import me.ghui.v2er.general.PreConditions;

/**
 * Created by ghui on 24/07/2017.
 */

public class GeneralError extends Throwable {

    private String response;
    private String message;

    public GeneralError(int errorCode, String message) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (PreConditions.notEmpty(message)) return message;
        return super.getMessage();
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
