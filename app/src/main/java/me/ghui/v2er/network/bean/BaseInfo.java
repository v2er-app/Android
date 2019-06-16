package me.ghui.v2er.network.bean;

import java.io.Serializable;

/**
 * Created by ghui on 21/06/2017.
 * Check whether the model is valid, if invalide try to find the reason from the rawResponse.
 * Such as login expired, no premission, etc.
 */

public abstract class BaseInfo implements IBase, Serializable {
    public String rawResponse;

    @Override
    public String getResponse() {
        return rawResponse;
    }

    @Override
    public void setResponse(String response) {
        rawResponse = response;
    }
}
