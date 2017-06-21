package me.ghui.v2er.module.login;

import android.content.Intent;
import android.support.annotation.BinderThread;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.injector.component.DaggerLoginComponent;
import me.ghui.v2er.injector.module.LoginModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.home.MainActivity;

/**
 * Created by ghui on 30/04/2017.
 */

public class LoginActivity extends BaseActivity<LoginContract.IPresenter> implements LoginContract.IView {

    @BindView(R.id.login_user_name_text_input_layout)
    TextInputLayout mUserInputLayout;
    @BindView(R.id.login_psw_text_input_layout)
    TextInputLayout mPswInputLayout;
    @BindView(R.id.login_go_btn)
    Button mLoginBtn;

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
        if (PreConditions.isEmpty(userName)) {
            mUserInputLayout.setError("请输入用户名");
            return;
        }

        if (PreConditions.isEmpty(psw)) {
            mPswInputLayout.setError("请输入密码");
            return;
        }
        mPresenter.login(userName, psw);
    }

    @Override
    public void onFetchLoginParamFailure() {
        toast("加载登录参数出错");
        // TODO: 21/06/2017 add a dialog to retry
        mLoginBtn.setEnabled(false);
    }

    @Override
    public void onFetchLoginParamSuccess() {
        mLoginBtn.setEnabled(true);
    }

    @Override
    public void onLoginSuccess() {
        toast("登录成功");
        Navigator.from(this).setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP).to(MainActivity.class).start();
        finish();
    }

    @Override
    public void onLoginFailure(String msg) {
        toast(msg);
    }
}
