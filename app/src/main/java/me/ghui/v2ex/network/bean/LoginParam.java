package me.ghui.v2ex.network.bean;

import java.util.HashMap;
import java.util.Map;

import me.ghui.v2ex.htmlpicker.annotations.Select;

/**
 * Created by ghui on 01/05/2017.
 */

@Select("input.sl")
public class LoginParam {
    @Select(value = "[type=text]", attr = "name")
    private String nameParam;
    @Select(value = "[type=password]", attr = "name")
    private String pswParam;

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

    @Override
    public String toString() {
        return "LoginParam{" +
                "nameParam='" + nameParam + '\'' +
                ", pswParam='" + pswParam + '\'' +
                '}';
    }

    public Map<String, String> toMap(String userName, String psw) {
        Map map = new HashMap<String, String>();
        map.put(nameParam, userName);
        map.put(pswParam, userName);
        return map;
    }
}
