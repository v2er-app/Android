package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.SpecialCareModule;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.drawer.care.SpecialCareActivity;

/**
 * Created by ghui on 17/05/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = SpecialCareModule.class)
public interface SpecialCareComponent {
    void inject(SpecialCareActivity activity);
}
