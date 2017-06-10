package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.TopicStarModule;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.drawer.star.TopicStarFragment;

/**
 * Created by ghui on 18/05/2017.
 */

@PerFragment
@Component(dependencies = AppComponent.class, modules = TopicStarModule.class)
public interface TopicStarComponent {
    void inject(TopicStarFragment fragment);
}
