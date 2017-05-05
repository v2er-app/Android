package me.ghui.v2ex.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.topic.TopicActivity;
import me.ghui.v2ex.module.topic.TopicAdapter;
import me.ghui.v2ex.module.topic.TopicContract;
import me.ghui.v2ex.module.topic.TopicPresenter;

/**
 * Created by ghui on 05/05/2017.
 */

@Module
public class TopicModule {

    private TopicActivity mView;

    public TopicModule(TopicActivity view) {
        mView = view;
    }

    @Provides
    public TopicAdapter provideAdapter() {
        return new TopicAdapter(mView);
    }

    @Provides
    @PerActivity
    public TopicContract.IPresenter providePresenter() {
        return new TopicPresenter(mView);
    }

}
