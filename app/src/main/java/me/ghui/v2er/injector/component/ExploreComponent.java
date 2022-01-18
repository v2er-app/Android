package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.ExploreModule;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.ExploreFragment;

/**
 * Created by ghui on 22/05/2017.
 */

@PerFragment
@Component(dependencies = AppComponent.class, modules = ExploreModule.class)
public interface ExploreComponent {
    void inject(ExploreFragment mExploreFragment);
}
