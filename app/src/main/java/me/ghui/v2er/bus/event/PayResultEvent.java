package me.ghui.v2er.bus.event;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PayResultEvent {
    public boolean isSuccess;
    public String msg;
    // wechat, google
    @PayWay
    public int payWay;

    @IntDef({PayWay.WECHAT_PAY, PayWay.GOOGLE_PLAY_PAY})
    public @interface PayWay {
        int WECHAT_PAY = 1;
        int GOOGLE_PLAY_PAY = 2;
    }


    public PayResultEvent(boolean isSuccess, @PayWay int payWay, String msg) {
        this.isSuccess = isSuccess;
        this.msg = msg;
        this.payWay = payWay;
    }
}
