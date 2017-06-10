package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.NodesNavModule;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.NodesNavFragment;

/**
 * Created by ghui on 22/05/2017.
 */

@PerFragment
@Component(dependencies = AppComponent.class, modules = NodesNavModule.class)
public interface NodesNavComponent {
    void inject(NodesNavFragment navFragment);
}
