package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.SearchModule;
import me.ghui.v2ex.injector.scope.PerFragment;
import me.ghui.v2ex.module.home.SearchFragment;

/**
 * Created by ghui on 05/05/2017.
 */

@PerFragment
@Component(dependencies = AppComponent.class, modules = SearchModule.class)
public interface SearchComponent {
    void inject(SearchFragment fragment);
}
