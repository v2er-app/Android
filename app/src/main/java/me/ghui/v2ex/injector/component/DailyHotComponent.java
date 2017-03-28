package me.ghui.v2ex.injector.component;

import dagger.Component;
import me.ghui.v2ex.injector.module.DailyHotModule;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotActivity;

/**
 * Created by ghui on 28/03/2017.
 */

@PerActivity
@Component(dependencies = AppComponent.class, modules = DailyHotModule.class)
public interface DailyHotComponent {
	void inject(DailyHotActivity dailyHotActivity);
}
