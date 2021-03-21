package me.ghui.v2er.module.pay;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import me.ghui.v2er.network.bean.BaseInfo;
import me.ghui.v2er.util.UserUtils;

public class OrderStatusInfo extends BaseInfo {
    @SerializedName("code")
    private int code;
    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private OrderInfo orderInfo;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public boolean paid() {
        return orderInfo != null && 1 == orderInfo.getPayStatus();
    }

    public ParseOrder toParseOrder() {
        if (orderInfo == null || TextUtils.isEmpty(orderInfo.orderNo)) return null;
        return null;
//        return ParseOrder.create()
//                .setId(orderInfo.outTradeNo)
//                .setYunGouOrderID(orderInfo.orderNo)
//                .setV2EXUserID(UserUtils.getUserID())
//                .setV2EXUserName(UserUtils.getUserName())
//                .setPrice(orderInfo.money);
    }

    public static class OrderInfo {
        @SerializedName("addTime")
        private String addTime;
        @SerializedName("attach")
        private String attach;
        @SerializedName("body")
        private String body;
        @SerializedName("mchid")
        private String mchid;
        @SerializedName("money")
        private String money;
        // 系统订单号
        @SerializedName("orderNo")
        private String orderNo;
        // 商户订单号 (商户自己生成的订单号）
        @SerializedName("outTradeNo")
        private String outTradeNo;
        // 支付单号
        @SerializedName("payNo")
        private String payNo;
        // 支付状态 0：未支付 1：已支付
        @SerializedName("payStatus")
        private int payStatus = -1;
        // 支付时间
        @SerializedName("payTime")
        private String payTime;
        // 支付方式
        @SerializedName("payType")
        private String payType;

        public String getAddTime() {
            return addTime;
        }

        public String getAttach() {
            return attach;
        }

        public String getBody() {
            return body;
        }

        public String getMchid() {
            return mchid;
        }

        public String getMoney() {
            return money;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public String getOutTradeNo() {
            return outTradeNo;
        }

        public String getPayNo() {
            return payNo;
        }

        public int getPayStatus() {
            return payStatus;
        }

        public String getPayTime() {
            return payTime;
        }

        public String getPayType() {
            return payType;
        }

        @Override
        public String toString() {
            return "OrderInfo{" +
                    "addTime='" + addTime + '\'' +
                    ", attach='" + attach + '\'' +
                    ", body='" + body + '\'' +
                    ", mchid='" + mchid + '\'' +
                    ", money='" + money + '\'' +
                    ", orderNo='" + orderNo + '\'' +
                    ", outTradeNo='" + outTradeNo + '\'' +
                    ", payNo='" + payNo + '\'' +
                    ", payStatus='" + payStatus + '\'' +
                    ", payTime='" + payTime + '\'' +
                    ", payType='" + payType + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OrderStatusInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", orderInfo=" + orderInfo +
                '}';
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
