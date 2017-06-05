package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.CreateTopicModule;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.create.CreateTopicActivity;

/**
 * Created by ghui on 27/05/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = CreateTopicModule.class)
public interface CreateTopicComponnet {
    void inject(CreateTopicActivity activity);
}
