package me.ghui.v2er.module.login;


import com.tencent.bugly.crashreport.CrashReport;

import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.DailyInfo;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.BaseInfo;
import me.ghui.v2er.network.bean.LoginParam;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.UserUtils;

/**
 * Created by ghui on 27/03/2017.
 */

public class LoginPresenter implements LoginContract.IPresenter {

    private LoginContract.IView mView;
    private LoginParam mLoginParam;

    public LoginPresenter(LoginContract.IView mView) {
        this.mView = mView;
    }

    @Override
    public void start() {
        APIService.get().loginParam()
                .compose(mView.rx(null))
                .subscribe(new GeneralConsumer<LoginParam>() {
                    @Override
                    public void onConsume(LoginParam loginParam) {
                        if (loginParam.isValid()) {
                            mLoginParam = loginParam;
                            mView.onFetchLoginParamSuccess();
                        } else {
                            mView.onFetchLoginParamFailure();
                        }
                    }
                });
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void login(String userName, String psw) {
        APIService.get().login(mLoginParam.toMap(userName, psw))
                .compose(mView.rx())
                .map(response -> response.body().string())
                .map(s -> {
                    DailyInfo resultInfo = APIService.fruit().fromHtml(s, DailyInfo.class);
                    if (!resultInfo.isValid()) {
                        return APIService.fruit().fromHtml(s, LoginParam.class);
                    }
                    return resultInfo;
                })
                .subscribe(new GeneralConsumer<BaseInfo>() {
                    @Override
                    public void onConsume(BaseInfo info) {
                        if (info instanceof DailyInfo) {
                            //login success
                            DailyInfo resultInfo = (DailyInfo) info;
                            UserUtils.saveLogin(UserInfo.build(resultInfo.getUserName(), resultInfo.getAvatar()));
                            mView.onLoginSuccess();
                            CrashReport.setUserId(resultInfo.getUserName());
                        } else {
                            //login failure
                            LoginParam loginParam = (LoginParam) info;
                            if (loginParam.isValid()) {
                                mLoginParam = loginParam;
                                mView.onLoginFailure("登录失败，用户名和密码无法匹配");
                            } else {
                                mView.onLoginFailure(App.get().getString(R.string.login_occur_unknown_error));
                            }
                        }
                    }
                });
    }

    @Override
    public void signInWithGoogle() {
        SignInWithGoogleActivity.open(mView.getContext(), mLoginParam.getOnce());
    }

}
