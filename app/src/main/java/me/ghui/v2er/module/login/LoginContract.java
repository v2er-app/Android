package me.ghui.v2er.module.login;

import me.ghui.v2er.module.base.BaseContract;

/**
 * Created by ghui on 01/05/2017.
 */

public class LoginContract {

    public interface IView extends BaseContract.IView {

        void onFetchLoginParamFailure();

        void onFetchLoginParamSuccess();

        void onLoginSuccess();

        void onLoginFailure(String msg);

        void onOccuredTwoStep();


    }

    public interface IPresenter extends BaseContract.IPresenter {
        void login(String userName, String psw);

        void signInWithGoogle();
    }
}
