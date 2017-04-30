package me.ghui.v2ex.module.login;

import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.widget.BaseToolBar;

/**
 * Created by ghui on 30/04/2017.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_user_name_et)
    EditText mUserEt;
    @BindView(R.id.login_psw_et)
    EditText mPswEt;

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        toolBar.setTitle("登录");
        toolBar.setElevation(0);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_login;
    }

    @OnClick(R.id.login_go_btn)
    void onLoginClicked() {

    }

}
