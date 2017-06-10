package me.ghui.v2er.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.login.LoginContract;
import me.ghui.v2er.module.login.LoginPresenter;

/**
 * Created by ghui on 01/05/2017.
 */

@Module
public class LoginModule {
    private final LoginContract.IView mView;

    public LoginModule(LoginContract.IView view) {
        mView = view;
    }


    @PerActivity
    @Provides
    public LoginContract.IPresenter providePresenter() {
        return new LoginPresenter(mView);
    }

}
