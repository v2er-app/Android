package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.TopicModule;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.topic.TopicActivity;

/**
 * Created by ghui on 05/05/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = TopicModule.class)
public interface TopicComponent {
    void inject(TopicActivity activity);
}
