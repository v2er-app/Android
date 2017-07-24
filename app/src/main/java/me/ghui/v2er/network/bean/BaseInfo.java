package me.ghui.v2er.network.bean;

import okhttp3.ResponseBody;

/**
 * Created by ghui on 21/06/2017.
 * Check whether the model is valid, if invalide try to find the reason from the rawResponse.
 * Such as login expired, no premission, etc.
 */

public abstract class BaseInfo implements IBase {
    public ResponseBody rawResponse;

    @Override
    public ResponseBody getResponse() {
        return rawResponse;
    }

    @Override
    public void setResponse(ResponseBody response) {
        rawResponse = response;
    }
}
