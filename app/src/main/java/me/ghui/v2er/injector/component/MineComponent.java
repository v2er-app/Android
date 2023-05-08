package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.MineModule;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.MineFragment;

@PerFragment
@Component(dependencies = AppComponent.class, modules = MineModule.class)
public interface MineComponent {
    void inject(MineFragment fragment);
}
