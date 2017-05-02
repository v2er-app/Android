package me.ghui.v2ex.module.login;

import me.ghui.v2ex.module.base.BaseContract;

/**
 * Created by ghui on 01/05/2017.
 */

public class LoginContract {

    public interface IView extends BaseContract.IView {

        void onLoginSuccess();
    }

    public interface IPresenter extends BaseContract.IPresenter {
        void login(String userName, String psw);
    }
}
