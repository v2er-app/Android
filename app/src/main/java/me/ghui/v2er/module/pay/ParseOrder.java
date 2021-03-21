package me.ghui.v2er.module.pay;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Order")
public class ParseOrder extends ParseObject {

    public ParseOrder setId(String orderId) {
        put("orderId", orderId);
        return this;
    }

    /**
     * 外部订单ID(yungou id)
     * @param outId
     * @return
     */
    public ParseOrder setOutId(String outId) {
        put("outOrderId", outId);
        return this;
    }

    public ParseOrder setUserName(String userName) {
        put("v2exUserName", userName);
        return this;
    }

    public ParseOrder setUserId(String userId) {
        put("v2exUserId", userId);
        return this;
    }

    public ParseOrder setPrice(String price) {
        put("price", price);
        return this;
    }

    public ParseOrder setPayTime(String payTime) {
        put("payTime", payTime);
        return this;
    }

    public String getId() {
        return getString("orderId");
    }

    public String getOutId() {
        return getString("outOrderId");
    }

    public String getUserName() {
        return getString("v2exUserName");
    }

    public String getUserId() {
        return getString("v2exUserId");
    }

    public String getPrice() {
        return getString("price");
    }

    public String getPayTime() {
        return getString("payTime");
    }


    public static ParseOrder create() {
        return new ParseOrder();
    }

}
