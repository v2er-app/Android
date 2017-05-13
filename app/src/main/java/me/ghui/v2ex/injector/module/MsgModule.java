package me.ghui.v2ex.injector.module;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.injector.scope.PerFragment;
import me.ghui.v2ex.module.home.MsgContract;
import me.ghui.v2ex.module.home.MsgFragment;
import me.ghui.v2ex.module.home.MsgPresenter;
import me.ghui.v2ex.network.Constants;
import me.ghui.v2ex.network.bean.NotificationInfo;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

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
                Glide.with(mView).load(Constants.HTTP_SCHEME + reply.getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.msg_title_tv, reply.getTitle());
                holder.setText(R.id.msg_content_tv, reply.getContent());
                holder.setText(R.id.time_tv, reply.getTime());
            }
        };
    }

    @PerFragment
    @Provides
    public MsgContract.IPresenter providePresenter() {
        return new MsgPresenter(mView);
    }

}
