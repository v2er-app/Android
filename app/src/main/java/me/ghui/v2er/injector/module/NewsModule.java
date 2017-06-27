package me.ghui.v2er.injector.module;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.NewsContract;
import me.ghui.v2er.module.home.NewsFragment;
import me.ghui.v2er.module.home.NewsPresenter;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

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
    public LoadMoreRecyclerView.Adapter<NewsInfo.Item> provideNewsAdapter() {
        return new CommonLoadMoreAdapter<NewsInfo.Item>(mView.getContext(), R.layout.common_list_item) {
            @Override
            protected void convert(ViewHolder holder, NewsInfo.Item item, int position) {
                Glide.with(mContext)
                        .load(item.getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, item.getUserName());
                holder.setText(R.id.time_tv, item.getTime());
                holder.setText(R.id.tagview, item.getTagName());
                holder.setText(R.id.title_tv, item.getTitle());
                holder.setText(R.id.comment_num_tv, "评论" + item.getReplies());
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(
                        v -> {
                            NewsInfo.Item item = getItem(holder.index());
                            UserHomeActivity.open(item.getUserName(), mContext,
                                    holder.getImgView(R.id.avatar_img), item.getAvatar());
                        },
                        R.id.avatar_img, R.id.user_name_tv);
                holder.setOnClickListener(v ->
                        NodeTopicActivity.open(getItem(holder.index()).getTagLink(),
                                mContext), R.id.tagview);
            }

            @Override
            protected boolean shouldAnimate() {
                return true;
            }
        };
    }

    @PerFragment
    @Provides
    public NewsContract.IPresenter provideNewsPresenter() {
        return new NewsPresenter(mView);
    }

}
