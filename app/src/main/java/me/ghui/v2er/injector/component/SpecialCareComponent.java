package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.SpecialCareModule;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.drawer.care.SpecialCareActivity;

/**
 * Created by ghui on 17/05/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = SpecialCareModule.class)
public interface SpecialCareComponent {
    void inject(SpecialCareActivity activity);
}
