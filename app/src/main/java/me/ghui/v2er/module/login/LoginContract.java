package me.ghui.v2er.module.login;

import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.network.bean.LoginParam;

/**
 * Created by ghui on 01/05/2017.
 */

public class LoginContract {

    public interface IView extends BaseContract.IView {

        void onFetchLoginParamFailure();

        void onFetchLoginParamSuccess(LoginParam loginParam);

        void onLoginSuccess();

        void onLoginFailure(String msg, boolean withProblem);

        void onOccuredTwoStep();


    }

    public interface IPresenter extends BaseContract.IPresenter {
        void login(String userName, String psw, String captcha);

        void signInWithGoogle();
    }
}
