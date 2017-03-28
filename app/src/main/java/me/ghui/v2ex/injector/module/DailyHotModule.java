package me.ghui.v2ex.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotAdapter;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotContract;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotPresenter;

/**
 * Created by ghui on 27/03/2017.
 */

@Module
public class DailyHotModule {

	private final DailyHotActivity mView;

	public DailyHotModule(DailyHotActivity view) {
		mView = view;
	}

	@Provides
	public DailyHotAdapter provideDailyHotAdapter() {
		return new DailyHotAdapter(mView);
	}

	@PerActivity
	@Provides
	public DailyHotContract.IDailyHotPresenter provideDailyHotPresenter() {
		return new DailyHotPresenter(mView);
	}

}
