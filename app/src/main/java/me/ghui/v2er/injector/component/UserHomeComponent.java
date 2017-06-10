package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.UserHomeModule;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.user.UserHomeActivity;

/**
 * Created by ghui on 01/06/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = UserHomeModule.class)
public interface UserHomeComponent {
    void inject(UserHomeActivity activity);
}
