package me.ghui.v2ex.module.user;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;

/**
 * Created by ghui on 04/05/2017.
 * 用户主页
 */

public class UserHomeActivity extends BaseActivity {

    public static final String USER_NAME_KEY = KEY("user_name");

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_user_home;
    }
}
