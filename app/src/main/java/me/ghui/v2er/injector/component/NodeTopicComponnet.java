package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.NodeTopicModule;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.node.NodeTopicActivity;

/**
 * Created by ghui on 27/05/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = NodeTopicModule.class)
public interface NodeTopicComponnet {
    void inject(NodeTopicActivity activity);
}
