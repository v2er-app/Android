package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.NodesNavModule;
import me.ghui.v2ex.module.home.NodesNavFragment;

/**
 * Created by ghui on 22/05/2017.
 */

@Component(dependencies = AppComponent.class, modules = NodesNavModule.class)
public interface NodesNavComponent {
    void inject(NodesNavFragment navFragment);
}
