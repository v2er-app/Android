package me.ghui.v2er.injector.module;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotContract;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotPresenter;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.DailyHotInfo;

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
                Picasso.with(mContext)
                        .load(item.getMember().getAvatar())
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, item.getMember().getUserName());
                holder.setText(R.id.time_tv, item.getTime());
                holder.setText(R.id.tagview, item.getNode().getTitle());
                holder.setText(R.id.title_tv, item.getTitle());
                holder.setText(R.id.comment_num_tv, "评论" + item.getReplies());
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> {
                            DailyHotInfo.Item.Member member = getItem(holder.index()).getMember();
                            UserHomeActivity.open(member.getUserName(), mContext, holder.getImgView(R.id.avatar_img), member.getAvatar());
                        },
                        R.id.avatar_img, R.id.user_name_tv);

                holder.setOnClickListener(v ->
                                NodeTopicActivity.open(getItem(holder.index()).getNode().getUrl(), mContext),
                        R.id.tagview);
            }
        };
    }

    @PerActivity
    @Provides
    public DailyHotContract.IPresenter provideDailyHotPresenter() {
        return new DailyHotPresenter(mView);
    }

}
