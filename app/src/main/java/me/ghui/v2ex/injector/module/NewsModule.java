package me.ghui.v2ex.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.injector.scope.PerFragment;
import me.ghui.v2ex.module.home.NewsAdapter;
import me.ghui.v2ex.module.home.NewsContract;
import me.ghui.v2ex.module.home.NewsFragment;
import me.ghui.v2ex.module.home.NewsPresenter;

/**
 * Created by ghui on 03/04/2017.
 */

@Module
public class NewsModule {

	private final NewsFragment mView;

	public NewsModule(NewsFragment view) {
		mView = view;
	}

	@Provides
	public NewsAdapter provideNewsAdapter() {
		return new NewsAdapter(mView.getContext());
	}

	@PerFragment
	@Provides
	public NewsContract.IPresenter provideNewsPresenter() {
		return new NewsPresenter(mView);
	}

}
