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
import me.ghui.v2ex.module.home.NewsContract;
import me.ghui.v2ex.module.home.NewsFragment;
import me.ghui.v2ex.module.home.NewsPresenter;
import me.ghui.v2ex.module.user.UserHomeActivity;
import me.ghui.v2ex.network.bean.NewsInfo;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

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
                        .load("https:" + item.getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, item.getUser());
                holder.setText(R.id.time_tv, item.getTime());
                holder.setText(R.id.tagview, item.getTagName());
                holder.setText(R.id.title_tv, item.getTitle());
                holder.setText(R.id.comment_num_tv, "评论" + item.getReplies());
            }

            @Override
            protected void bindListener(ViewHolder viewHolder, int viewType) {
                super.bindListener(viewHolder, viewType);
                viewHolder.setOnClickListener(
                        v -> Navigator.from(mContext)
                                .to(UserHomeActivity.class)
                                .putExtra(UserHomeActivity.USER_NAME_KEY, getItem(viewHolder.index()).getUser())
                                .start(),
                        R.id.avatar_img, R.id.user_name_tv);
            }
        };
    }

    @PerFragment
    @Provides
    public NewsContract.IPresenter provideNewsPresenter() {
        return new NewsPresenter(mView);
    }

}
