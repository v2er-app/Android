package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.UserHomeModule;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.user.UserHomeActivity;

/**
 * Created by ghui on 01/06/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = UserHomeModule.class)
public interface UserHomeComponent {
    void inject(UserHomeActivity activity);
}
