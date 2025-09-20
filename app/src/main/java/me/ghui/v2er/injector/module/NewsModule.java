package me.ghui.v2er.injector.module;

import android.widget.ImageView;
import android.widget.TextView;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.NewsContract;
import me.ghui.v2er.module.home.NewsFragment;
import me.ghui.v2er.module.home.NewsPresenter;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.util.ViewHolderFontHelper;
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
            protected boolean shouldAnimate() {
                return true;
            }

            @Override
            protected void convert(ViewHolder holder, NewsInfo.Item item, int position) {
//                Picasso.with(mContext)
//                        .load(item.getAvatar())
//                        .placeholder(R.drawable.avatar_placeholder_drawable)
//                        .into((ImageView) holder.getView(R.id.avatar_img));
                GlideApp.with(mContext)
                        .load(item.getAvatar())
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into(holder.getImgView(R.id.avatar_img));

                // Use centralized font helper
                ViewHolderFontHelper.applyCommonListItemFonts(holder,
                        item.getTitle(),
                        item.getUserName(),
                        item.getTime(),
                        item.getTagName(),
                        "评论" + item.getReplies());
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(
                        v -> {
                            NewsInfo.Item item = getItem(holder.index());
                            ImageView avatarImg = holder.getImgView(R.id.avatar_img);
                            UserHomeActivity.open(item.getUserName(), mContext,
                                    avatarImg, item.getAvatar());
                            L.d("NewsAvatar:1 " + item.getAvatar());
                        },
                        R.id.avatar_img, R.id.user_name_tv);
                holder.setOnClickListener(v ->
                        NodeTopicActivity.open(getItem(holder.index()).getTagLink(),
                                mContext), R.id.tagview);
            }

        };
    }

    @PerFragment
    @Provides
    public NewsContract.IPresenter provideNewsPresenter() {
        return new NewsPresenter(mView);
    }

}
