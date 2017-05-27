package me.ghui.v2ex.injector.module;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonAdapter;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.injector.scope.PerActivity;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotContract;
import me.ghui.v2ex.module.drawer.dailyhot.DailyHotPresenter;
import me.ghui.v2ex.module.node.NodeTopicActivity;
import me.ghui.v2ex.module.user.UserHomeActivity;
import me.ghui.v2ex.network.bean.DailyHotInfo;
import me.ghui.v2ex.util.DateUtils;

/**
 * Created by ghui on 27/03/2017.
 */

@Module
public class DailyHotModule {

    private final DailyHotActivity mView;

    public DailyHotModule(DailyHotActivity view) {
        mView = view;
    }

    @Provides
    public CommonAdapter<DailyHotInfo.Item> provideDailyHotAdapter() {
        return new CommonAdapter<DailyHotInfo.Item>(mView, R.layout.common_list_item) {

            @Override
            protected void convert(ViewHolder holder, DailyHotInfo.Item item, int position) {
                Glide.with(mContext)
                        .load("https:" + item.getMember().getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, item.getMember().getUserName());
                holder.setText(R.id.time_tv, DateUtils.parseDate(item.getTime()));
                holder.setText(R.id.tagview, item.getNode().getTitle());
                holder.setText(R.id.title_tv, item.getTitle());
                holder.setText(R.id.comment_num_tv, "评论" + item.getReplies());
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(
                        v -> Navigator.from(mContext)
                                .to(UserHomeActivity.class)
                                .putExtra(UserHomeActivity.USER_NAME_KEY,
                                        getItem(holder.index()).getId())
                                .start(),
                        R.id.avatar_img, R.id.user_name_tv);
                holder.setOnClickListener(v -> Navigator.from(mContext)
                        .to(NodeTopicActivity.class)
                        .putExtra(NodeTopicActivity.TAG_LINK_KEY,
                                getItem(holder.index()).getNode().getName())
                        .start(), R.id.tagview);
            }
        };
    }

    @PerActivity
    @Provides
    public DailyHotContract.IPresenter provideDailyHotPresenter() {
        return new DailyHotPresenter(mView);
    }

}
