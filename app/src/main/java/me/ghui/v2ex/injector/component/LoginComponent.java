package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.LoginModule;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.login.LoginActivity;

/**
 * Created by ghui on 01/05/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = LoginModule.class)
public interface LoginComponent {
    void inject(LoginActivity activity);
}
