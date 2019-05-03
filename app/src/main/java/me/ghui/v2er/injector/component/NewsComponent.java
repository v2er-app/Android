package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.NewsModule;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.NewsFragment;

/**
 * Created by ghui on 03/04/2017.
 */

@PerFragment
@Component(dependencies = AppComponent.class, modules = NewsModule.class)
public interface NewsComponent {
    void inject(NewsFragment fragment);
}
