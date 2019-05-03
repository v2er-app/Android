package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.MsgModule;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.MsgFragment;

/**
 * Created by ghui on 03/04/2017.
 */

@PerFragment
@Component(dependencies = AppComponent.class, modules = MsgModule.class)
public interface MsgComponent {
    void inject(MsgFragment fragment);
}
