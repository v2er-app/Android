package me.ghui.v2er.module.pay;

import com.google.gson.annotations.SerializedName;

import me.ghui.v2er.network.bean.BaseInfo;

public class WechatH5PayResultInfo extends BaseInfo {
    private int code;
    private String msg;
    @SerializedName("data")
    private String h5Url;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getH5Url() {
        return h5Url;
    }

    @Override
    public String toString() {
        return "WechatH5PayInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", h5Url='" + h5Url + '\'' +
                '}';
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
