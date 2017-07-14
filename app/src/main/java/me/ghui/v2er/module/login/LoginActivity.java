package me.ghui.v2er.module.login;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

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
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

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
    //登录参数加载成功标识
    private boolean mHasLoaded;

    @Override
    protected void startInject() {
        DaggerLoginComponent.builder()
                .appComponent(getAppComponent())
                .loginModule(new LoginModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        toolBar.setElevation(0);
        toolBar.inflateMenu(R.menu.login_toolbar_menu);
        toolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_register) {
                Utils.openWap(Constants.BASE_URL + "/signup?r=ghui", this);
            } else if (item.getItemId() == R.id.action_forgot_psw) {
                Utils.openWap(Constants.BASE_URL + "/forgot", this);
            } else if (item.getItemId() == R.id.action_faq) {
                Utils.openWap(Constants.BASE_URL + "/faq", this);
            } else if (item.getItemId() == R.id.action_about) {
                Utils.openWap(Constants.BASE_URL + "/about", this);
            }
            return true;
        });
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
        if (!mHasLoaded) {
            toast("登录参数正在加载，请稍后...");
            return;
        }
        mPresenter.login(userName, psw);
    }

    @Override
    public void onFetchLoginParamFailure() {
        mHasLoaded = false;
        toast("加载登录参数出错");
        new ConfirmDialog.Builder(getActivity())
                .title("加载登录参数出错")
                .msg("是否重试")
                .negativeText(R.string.cancel)
                .positiveText(R.string.ok, dialog -> autoLoad()).build().show();
    }

    @Override
    public void onFetchLoginParamSuccess() {
        Logger.d("加载登录参数成功");
        mHasLoaded = true;
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
