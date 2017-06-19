package me.ghui.v2er.module.login;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.LoginParam;
import me.ghui.v2er.network.bean.LoginResultInfo;
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
                        mLoginParam = loginParam;
                    }
                });
    }

    @Override
    public void login(String userName, String psw) {
        APIService.get().login(mLoginParam.toMap(userName, psw))
                .compose(mView.rx())
                .subscribe(new GeneralConsumer<LoginResultInfo>() {
                    @Override
                    public void onConsume(LoginResultInfo loginResultInfo) {
                        Logger.d("loginResultInfo: " + loginResultInfo);
                        UserUtils.saveLogin(UserInfo.build(loginResultInfo.getUserName(), loginResultInfo.getAvatar()));
                        mView.onLoginSuccess();
                    }
                });
    }

}
