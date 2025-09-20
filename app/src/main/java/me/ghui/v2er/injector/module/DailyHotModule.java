package me.ghui.v2er.injector.module;

import android.widget.ImageView;
import android.widget.TextView;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotActivity;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotContract;
import me.ghui.v2er.module.drawer.dailyhot.DailyHotPresenter;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.DailyHotInfo;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.util.FontSizeUtil;
import android.util.TypedValue;

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
                GlideApp.with(mContext)
                        .load(item.getMember().getAvatar())
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into((ImageView) holder.getView(R.id.avatar_img));

                // Apply dynamic font sizing
                TextView titleTv = holder.getTextView(R.id.title_tv);
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getTitleSize());
                titleTv.setText(item.getTitle());

                TextView userNameTv = holder.getTextView(R.id.user_name_tv);
                userNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                userNameTv.setText(item.getMember().getUserName());

                TextView timeTv = holder.getTextView(R.id.time_tv);
                timeTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                timeTv.setText(item.getTime());

                TextView tagTv = holder.getTextView(R.id.tagview);
                tagTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                tagTv.setText(item.getNode().getTitle());

                TextView commentTV = holder.getTextView(R.id.comment_num_tv);
                commentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                commentTV.setText("评论" + item.getReplies());
                ViewUtils.highlightCommentNum(commentTV);
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
