package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.NodeTopicModule;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.node.NodeTopicActivity;

/**
 * Created by ghui on 27/05/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = NodeTopicModule.class)
public interface NodeTopicComponnet {
    void inject(NodeTopicActivity activity);
}
