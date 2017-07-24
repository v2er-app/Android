package me.ghui.v2er.network.bean;

import okhttp3.ResponseBody;

/**
 * Created by ghui on 24/07/2017.
 */

public interface IBase {
    ResponseBody getResponse();

    void setResponse(ResponseBody response);

    /**
     * 某个接口返回业务上的合法性
     *
     * @return
     */
    boolean isValid();
}
