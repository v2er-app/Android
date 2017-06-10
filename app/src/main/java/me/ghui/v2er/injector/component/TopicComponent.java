package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.TopicModule;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.topic.TopicActivity;

/**
 * Created by ghui on 05/05/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = TopicModule.class)
public interface TopicComponent {
    void inject(TopicActivity activity);
}
