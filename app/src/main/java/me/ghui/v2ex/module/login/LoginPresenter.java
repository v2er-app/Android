package me.ghui.v2ex.module.login;

import com.orhanobut.logger.Logger;

import me.ghui.v2ex.network.APIService;
import me.ghui.v2ex.network.bean.LoginParam;
import me.ghui.v2ex.network.bean.UserInfo;
import me.ghui.v2ex.util.UserUtils;

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
                .subscribe(loginParam -> mLoginParam = loginParam);
    }

    @Override
    public void login(String userName, String psw) {
        APIService.get().login(mLoginParam.toMap(userName, psw))
                .compose(mView.rx())
                .subscribe(result -> {
                            Logger.d("loginResultInfo: " + result);
                            UserUtils.saveLogin(UserInfo.build(result.getUserName(), result.getAvatar()));
                            mView.onLoginSuccess();
                        }
                );
    }

}
