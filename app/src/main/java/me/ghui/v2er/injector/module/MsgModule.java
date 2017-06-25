package me.ghui.v2er.injector.module;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.home.MsgContract;
import me.ghui.v2er.module.home.MsgFragment;
import me.ghui.v2er.module.home.MsgPresenter;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.NotificationInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

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
                Glide.with(mView).load(reply.getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                CharSequence titleWithUserName = Utils.highlight(reply.getName() + " " + reply.getTitle(),
                        true, new int[]{0, reply.getName().length()});
                holder.setText(R.id.msg_title_tv, titleWithUserName);
                holder.getView(R.id.msg_content_tv);
                if (!PreConditions.isEmpty(reply.getContent())) {
                    holder.getView(R.id.msg_content_tv).setVisibility(View.VISIBLE);
                    holder.setText(R.id.msg_content_tv, reply.getContent());
                } else {
                    holder.getView(R.id.msg_content_tv).setVisibility(View.GONE);
                }
                holder.setText(R.id.time_tv, reply.getTime());
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
