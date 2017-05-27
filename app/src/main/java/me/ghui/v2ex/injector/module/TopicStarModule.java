package me.ghui.v2ex.injector.module;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.injector.scope.PerFragment;
import me.ghui.v2ex.module.drawer.star.TopicStarContract;
import me.ghui.v2ex.module.drawer.star.TopicStarFragment;
import me.ghui.v2ex.module.drawer.star.TopicStarPresenter;
import me.ghui.v2ex.module.node.NodeTopicActivity;
import me.ghui.v2ex.module.user.UserHomeActivity;
import me.ghui.v2ex.network.Constants;
import me.ghui.v2ex.network.bean.TopicStarInfo;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 17/05/2017.
 */

@Module
public class TopicStarModule {
    private TopicStarFragment mView;

    public TopicStarModule(TopicStarFragment view) {
        mView = view;
    }

    @Provides
    public LoadMoreRecyclerView.Adapter<TopicStarInfo.Item> provideAdapter() {
        return new CommonLoadMoreAdapter<TopicStarInfo.Item>(mView.getContext(), R.layout.common_list_item) {
            @Override
            protected void convert(ViewHolder holder, TopicStarInfo.Item item, int position) {
                Glide.with(mContext)
                        .load(Constants.HTTP_SCHEME + item.getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, item.getUserName());
                holder.setText(R.id.time_tv, item.getTime());
                holder.setText(R.id.tagview, item.getTag());
                holder.setText(R.id.title_tv, item.getTitle());
                holder.setText(R.id.comment_num_tv, "评论" + item.getCommentNum());
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(
                        v -> Navigator.from(mContext)
                                .to(UserHomeActivity.class)
                                .putExtra(UserHomeActivity.USER_NAME_KEY,
                                        getItem(holder.index()).getUserName())
                                .start(),
                        R.id.avatar_img, R.id.user_name_tv);
                holder.setOnClickListener(v -> Navigator.from(mContext)
                        .to(NodeTopicActivity.class)
                        .putExtra(NodeTopicActivity.TAG_LINK_KEY,
                                getItem(holder.index()).getTagLink())
                        .start(), R.id.tagview);
            }
        };
    }

    @PerFragment
    @Provides
    public TopicStarContract.IPresenter providePresenter() {
        return new TopicStarPresenter(mView);
    }

}
