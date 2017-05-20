package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.NodeStarModule;
import me.ghui.v2ex.injector.scope.PerFragment;
import me.ghui.v2ex.module.drawer.star.NodeStarFragment;

/**
 * Created by ghui on 18/05/2017.
 */

@PerFragment
@Component(dependencies = AppComponent.class, modules = NodeStarModule.class)
public interface NodeStarComponent {
    void inject(NodeStarFragment fragment);
}
