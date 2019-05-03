package me.ghui.v2er.injector.component;

import dagger.Component;
import me.ghui.v2er.injector.module.DailyHotModule;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotActivity;

/**
 * Created by ghui on 28/03/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = DailyHotModule.class)
public interface DailyHotComponent {
    void inject(DailyHotActivity dailyHotActivity);
}
