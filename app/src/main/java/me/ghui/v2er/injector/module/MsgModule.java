package me.ghui.v2er.injector.module;

import android.view.View;
import android.widget.ImageView;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.MsgContract;
import me.ghui.v2er.module.home.MsgFragment;
import me.ghui.v2er.module.home.MsgPresenter;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.NotificationInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.ViewHolderFontHelper;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.richtext.RichText;
import android.widget.TextView;

/**
 * Created by ghui on 10/05/2017.
 */

@Module
public class MsgModule {

    private MsgFragment mView;

    public MsgModule(MsgFragment view) {
        mView = view;
    }

    @Provides
    public LoadMoreRecyclerView.Adapter<NotificationInfo.Reply> provideAdapter() {
        return new CommonLoadMoreAdapter<NotificationInfo.Reply>(mView.getContext(), R.layout.notification_item) {
            @Override
            protected void convert(ViewHolder holder, NotificationInfo.Reply reply, int position) {
                GlideApp.with(mView.getContext()).load(reply.getAvatar())
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into((ImageView) holder.getView(R.id.avatar_img));

                // Apply font sizing
                CharSequence titleWithUserName = Utils.highlight(reply.getName() + " " + reply.getTitle(),
                        true, new int[]{0, reply.getName().length()});
                TextView titleTv = holder.getTextView(R.id.msg_title_tv);
                titleTv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getTitleSize());
                titleTv.setText(titleWithUserName);

                TextView contentTv = holder.getTextView(R.id.msg_content_tv);
                if (!Check.isEmpty(reply.getContent())) {
                    contentTv.setVisibility(View.VISIBLE);
                    contentTv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getContentSize());
                    RichText.from(reply.getContent()).into(contentTv);
                } else {
                    contentTv.setVisibility(View.GONE);
                }

                ViewHolderFontHelper.setTextWithSize(holder, R.id.time_tv,
                        reply.getTime(), FontSizeUtil.getSubTextSize());
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> {
                    UserHomeActivity.open(getItem(holder.index()).getName(), mContext, v, getItem(holder.index()).getAvatar());
                }, R.id.avatar_img);
            }
        };

    }

    @PerFragment
    @Provides
    public MsgContract.IPresenter providePresenter() {
        return new MsgPresenter(mView);
    }

}
