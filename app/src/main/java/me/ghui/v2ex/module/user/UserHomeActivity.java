package me.ghui.v2ex.module.user;

import android.content.Intent;
import android.widget.TextView;

import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;

/**
 * Created by ghui on 04/05/2017.
 * 用户主页
 */

public class UserHomeActivity extends BaseActivity {
    // TODO: 25/05/2017  

    public static final String USER_NAME_KEY = KEY("user_name");
    private String mUserName;

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_user_home;
    }

    @Override
    protected void parseExtras(Intent intent) {
        mUserName = intent.getStringExtra(USER_NAME_KEY);
    }

    @Override
    protected void init() {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.append(": " + mUserName);
    }
}
