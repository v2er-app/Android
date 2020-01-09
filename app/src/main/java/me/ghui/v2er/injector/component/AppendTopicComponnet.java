package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.AppendTopicModule;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.append.AppendTopicActivity;

@PerActivity
@Component(dependencies = AppComponent.class, modules = AppendTopicModule.class)
public interface AppendTopicComponnet {
    void inject(AppendTopicActivity appendTopicActivity);
}


