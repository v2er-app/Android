package me.ghui.v2ex.network.bean;

import java.util.HashMap;
import java.util.Map;

import me.ghui.v2ex.htmlpicker.annotations.Select;

/**
 * Created by ghui on 01/05/2017.
 */

public class LoginParam {
    @Select(value = "input[type=text]", attr = "name")
    private String nameParam;
    @Select(value = "input[type=password]", attr = "name")
    private String pswParam;
    @Select(value = "input[name=once]", attr = "value")
    private String once;

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
}
