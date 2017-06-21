package me.ghui.v2er.module.login;


import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.IValid;
import me.ghui.v2er.network.bean.LoginParam;
import me.ghui.v2er.network.bean.LoginResultInfo;
import me.ghui.v2er.network.bean.UserInfo;
import me.ghui.v2er.util.UserUtils;
import okhttp3.ResponseBody;
import retrofit2.Response;

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
                    LoginResultInfo resultInfo = APIService.fruit().fromHtml(s, LoginResultInfo.class);
                    if (!resultInfo.isValid()) {
                        return APIService.fruit().fromHtml(s, LoginParam.class);
                    }
                    return resultInfo;
                })
                .subscribe(new GeneralConsumer<Object>() {
                    @Override
                    public void onConsume(Object response) {
                        if (response instanceof LoginResultInfo) {
                            //login success
                            LoginResultInfo resultInfo = (LoginResultInfo) response;
                            UserUtils.saveLogin(UserInfo.build(resultInfo.getUserName(), resultInfo.getAvatar()));
                            mView.onLoginSuccess();
                        } else {
                            //login failure
                            LoginParam loginParam = (LoginParam) response;
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

}
