package me.ghui.v2er.injector.module;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dagger.Module;
import dagger.Provides;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.drawer.care.SpecialCareActivity;
import me.ghui.v2er.module.drawer.care.SpecialCareContract;
import me.ghui.v2er.module.drawer.care.SpecialCarePresenter;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.CareInfo;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 16/05/2017.
 */

@Module
public class SpecialCareModule {
    private SpecialCareActivity mView;

    public SpecialCareModule(SpecialCareActivity view) {
        mView = view;
    }


    @Provides
    public LoadMoreRecyclerView.Adapter<CareInfo.Item> provideAdapter() {
        return new CommonLoadMoreAdapter<CareInfo.Item>(mView, R.layout.common_list_item) {
            @Override
            protected void convert(ViewHolder holder, CareInfo.Item item, int position) {
                GlideApp.with(mContext)
                        .load(item.getAvatar())
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, item.getUserName());
                // TODO: 2018/6/9 time is null
                if (Check.isEmpty(item.getTime())) {
                    holder.getView(R.id.time_tv).setVisibility(View.INVISIBLE);
                } else {
                    holder.getView(R.id.time_tv).setVisibility(View.VISIBLE);
                    holder.setText(R.id.time_tv, item.getTime());
                }
                holder.setText(R.id.tagview, item.getTagName());
                holder.setText(R.id.title_tv, item.getTitle());
                TextView commentTV = holder.getTextView(R.id.comment_num_tv);
                commentTV.setText("评论" + item.getComentNum());
                ViewUtils.highlightCommentNum(commentTV);
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(
                        v -> {
                            CareInfo.Item item = getItem(holder.index());
                            UserHomeActivity.open(item.getUserName(), mContext, v, item.getAvatar());
                        },
                        R.id.avatar_img, R.id.user_name_tv);
                holder.setOnClickListener(v ->
                        NodeTopicActivity.open(getItem(holder.index()).getTagLink(), mContext), R.id.tagview);
            }
        };
    }

    @Provides
    @PerActivity
    public SpecialCareContract.IPresenter providePresenter() {
        return new SpecialCarePresenter(mView);
    }

}
