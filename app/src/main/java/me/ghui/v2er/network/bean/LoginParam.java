package me.ghui.v2er.network.bean;

import java.util.HashMap;
import java.util.Map;

import me.ghui.fruit.annotations.Pick;
import me.ghui.v2er.general.PreConditions;


/**
 * Created by ghui on 01/05/2017.
 */

public class LoginParam implements IBaseInfo {
    @Pick(value = "input[type=text]", attr = "name")
    private String nameParam;
    @Pick(value = "input[type=password]", attr = "name")
    private String pswParam;
    @Pick(value = "input[name=once]", attr = "value")
    private String once;

    @Override
    public String toString() {
        return "LoginParam{" +
                "nameParam='" + nameParam + '\'' +
                ", pswParam='" + pswParam + '\'' +
                ", once='" + once + '\'' +
                '}';
    }

    public String getNameParam() {
        return nameParam;
    }

    public void setNameParam(String nameParam) {
        this.nameParam = nameParam;
    }

    public String getPswParam() {
        return pswParam;
    }

    public void setPswParam(String pswParam) {
        this.pswParam = pswParam;
    }

    public String getOnce() {
        return once;
    }

    public void setOnce(String once) {
        this.once = once;
    }

    public Map<String, String> toMap(String userName, String psw) {
        Map map = new HashMap<String, String>();
        map.put(nameParam, userName);
        map.put(pswParam, psw);
        map.put("once", once);
        map.put("next", "/mission/daily");
        return map;
    }

    @Override
    public boolean isValid() {
        return PreConditions.notEmpty(nameParam, pswParam, once);
    }
}
