package me.ghui.v2er.network.bean;

import java.util.HashMap;
import java.util.Map;

import me.ghui.fruit.Attrs;
import me.ghui.fruit.annotations.Pick;
import me.ghui.toolbox.android.Check;


/**
 * Created by ghui on 01/05/2017.
 */

public class LoginParam extends BaseInfo {
    @Pick(value = "input[type=text]", attr = "name")
    private String nameParam;
    @Pick(value = "input[type=password]", attr = "name")
    private String pswParam;
    @Pick(value = "input[name=once]", attr = "value")
    private String once;
    @Pick(value = "input[placeholder*=验证码]", attr = "name")
    private String captchaParam;
    @Pick(value = "div.problem", attr = Attrs.INNER_HTML)
    private String problem;

    @Override
    public String toString() {
        return "LoginParam{" +
                "nameParam='" + nameParam + '\'' +
                ", pswParam='" + pswParam + '\'' +
                ", once='" + once + '\'' +
                ", captureParam='" + captchaParam + '\'' +
                ", problem='" + problem + '\'' +
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

    public String getCaptchaParam() {
        return captchaParam;
    }

    public boolean needCaptcha() {
        return Check.notEmpty(captchaParam);
    }

    public String getProblem() {
        return problem;
    }

    public Map<String, String> toMap(String userName, String psw, String captcha) {
        Map map = new HashMap<String, String>();
        map.put(nameParam, userName);
        map.put(pswParam, psw);
        map.put(captchaParam, captcha);
        map.put("once", once);
        map.put("next", "/mission/daily");
        return map;
    }

    @Override
    public boolean isValid() {
        return Check.notEmpty(nameParam, pswParam, once);
    }
}
