package me.ghui.v2ex.injector.module;

import javax.annotation.Nullable;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.ItemViewDelegate;
import me.ghui.v2ex.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.module.node.NodeTopicActivity;
import me.ghui.v2ex.module.user.UserHomeActivity;
import me.ghui.v2ex.module.user.UserHomeContract;
import me.ghui.v2ex.module.user.UserHomePresenter;
import me.ghui.v2ex.network.bean.UserPageInfo;

/**
 * Created by ghui on 01/06/2017.
 */

@Module
public class UserHomeModule {

    private UserHomeActivity mActivity;

    public UserHomeModule(UserHomeActivity activity) {
        mActivity = activity;
    }

    @Provides
    public UserHomeContract.IPresenter providePresenter() {
        return new UserHomePresenter(mActivity);
    }

    @Provides
    public MultiItemTypeAdapter<UserPageInfo.Item> provideAdapter() {
        MultiItemTypeAdapter<UserPageInfo.Item> multiItemTypeAdapter = new MultiItemTypeAdapter<UserPageInfo.Item>(mActivity) {
            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> Navigator.from(mContext)
                        .to(NodeTopicActivity.class)
                        .putExtra(NodeTopicActivity.TAG_LINK_KEY,
                                ((UserPageInfo.TopicItem) getItem(holder.index())).getTagLink())
                        .start(), R.id.tagview);

            }
        };
        multiItemTypeAdapter.addItemViewDelegate(new ItemViewDelegate<UserPageInfo.Item>(mActivity) {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.user_page_topic_list_item;
            }

            @Override
            public boolean isForViewType(@Nullable UserPageInfo.Item item, int position) {
                return item instanceof UserPageInfo.TopicItem;
            }

            @Override
            public void convert(ViewHolder holder, UserPageInfo.Item item, int position) {
                UserPageInfo.TopicItem topicItem = (UserPageInfo.TopicItem) item;
                holder.setText(R.id.user_name_tv, topicItem.getUserName());
                holder.setText(R.id.time_tv, topicItem.getTime());
                holder.setText(R.id.tagview, topicItem.getTag());
                holder.setText(R.id.title_tv, topicItem.getTitle());
                holder.setText(R.id.comment_num_tv, "评论" + topicItem.getReplyNum());
            }
        });

        multiItemTypeAdapter.addItemViewDelegate(new ItemViewDelegate<UserPageInfo.Item>(mActivity) {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.user_home_page_reply_item;
            }

            @Override
            public boolean isForViewType(@Nullable UserPageInfo.Item item, int position) {
                return item instanceof UserPageInfo.ReplyItem;
            }

            @Override
            public void convert(ViewHolder holder, UserPageInfo.Item item, int position) {
                UserPageInfo.ReplyItem replyItem = (UserPageInfo.ReplyItem) item;
                holder.setText(R.id.reply_title_tv, replyItem.getTitle());
                holder.setText(R.id.reply_content_tv, replyItem.getContent());
                holder.setText(R.id.reply_time_tv, replyItem.getTime());
            }
        });

        return multiItemTypeAdapter;
    }
}
