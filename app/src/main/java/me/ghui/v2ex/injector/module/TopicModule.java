package me.ghui.v2ex.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.node.NodeTopicActivity;
import me.ghui.v2ex.module.topic.TopicActivity;
import me.ghui.v2ex.module.topic.TopicContract;
import me.ghui.v2ex.module.topic.TopicHeaderItemDelegate;
import me.ghui.v2ex.module.topic.TopicPresenter;
import me.ghui.v2ex.module.topic.TopicReplyItemDelegate;
import me.ghui.v2ex.module.user.UserHomeActivity;
import me.ghui.v2ex.network.bean.TopicInfo;
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
        LoadMoreRecyclerView.Adapter adapter = new LoadMoreRecyclerView.Adapter(mView) {
            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> {
                            String userName;
                            if (v.getId() == R.id.avatar_img || v.getId() == R.id.user_name_tv) {
                                userName = ((TopicInfo.HeaderInfo) getItem(holder.index())).getUserName();
                            } else {
                                userName = ((TopicInfo.Reply) getItem(holder.index())).getUserName();
                            }
                            UserHomeActivity.open(userName, mContext);
                        },
                        R.id.avatar_img, R.id.user_name_tv, R.id.reply_avatar_img, R.id.reply_user_name_tv);

                holder.setOnClickListener(v ->
                        NodeTopicActivity.open(((TopicInfo.HeaderInfo) getItem(holder.index())).getTagLink(), mContext), R.id.tagview);
            }
        };
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
