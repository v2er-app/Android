package me.ghui.v2er.module.pay;

import com.google.gson.annotations.SerializedName;

import me.ghui.v2er.network.bean.BaseInfo;

public class WechatH5PayResultInfo extends BaseInfo {
    // 0: success
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("payUrl")
    private String h5Url;
    @SerializedName("orderId")
    private String orderId;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getPayUrl() {
        return h5Url;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "WechatH5PayResultInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", h5Url='" + h5Url + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
