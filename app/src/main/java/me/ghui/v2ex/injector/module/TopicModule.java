package me.ghui.v2ex.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.topic.TopicReplyItemDelegate;
import me.ghui.v2ex.module.topic.TopicActivity;
import me.ghui.v2ex.module.topic.TopicContract;
import me.ghui.v2ex.module.topic.TopicHeaderItemDelegate;
import me.ghui.v2ex.module.topic.TopicPresenter;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

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
    public LoadMoreRecyclerView.Adapter provideAdapter() {
        LoadMoreRecyclerView.Adapter adapter = new LoadMoreRecyclerView.Adapter(mView);
        adapter.addItemViewDelegate(new TopicHeaderItemDelegate(mView));
        adapter.addItemViewDelegate(new TopicReplyItemDelegate(mView));
        return adapter;
    }

    @Provides
    @PerActivity
    public TopicContract.IPresenter providePresenter() {
        return new TopicPresenter(mView);
    }

}
