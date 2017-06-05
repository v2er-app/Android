package me.ghui.v2ex.module.login;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2ex.R;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.injector.component.DaggerLoginComponent;
import me.ghui.v2ex.injector.module.LoginModule;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.module.home.MainActivity;
import me.ghui.v2ex.util.Utils;

/**
 * Created by ghui on 30/04/2017.
 */

public class LoginActivity extends BaseActivity<LoginContract.IPresenter> implements LoginContract.IView {

    @BindView(R.id.login_user_name_text_input_layout)
    TextInputLayout mUserInputLayout;
    @BindView(R.id.login_psw_text_input_layout)
    TextInputLayout mPswInputLayout;

    @Override
    protected void startInject() {
        DaggerLoginComponent.builder()
                .appComponent(getAppComponent())
                .loginModule(new LoginModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void configToolBar(Toolbar toolBar) {
        super.configToolBar(toolBar);
        toolBar.setElevation(0);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_login;
    }

    @Override
    protected void autoLoad() {
        mPresenter.start();
    }

    @OnClick(R.id.login_go_btn)
    void onLoginClicked() {
        String userName = mUserInputLayout.getEditText().getText().toString();
        String psw = mPswInputLayout.getEditText().getText().toString();
        if (Utils.isEmpty(userName)) {
            mUserInputLayout.setError("请输入用户名");
            return;
        }

        if (Utils.isEmpty(psw)) {
            mPswInputLayout.setError("请输入密码");
            return;
        }

        mPresenter.login(userName, psw);
    }

    @Override
    public void onLoginSuccess() {
        Logger.d("--- Login success ---");
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        Navigator.from(this).to(MainActivity.class).start();
    }
}
