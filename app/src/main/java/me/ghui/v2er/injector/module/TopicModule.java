package me.ghui.v2er.injector.module;

import android.widget.Toast;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.module.topic.TopicContract;
import me.ghui.v2er.module.topic.TopicHeaderItemDelegate;
import me.ghui.v2er.module.topic.TopicPresenter;
import me.ghui.v2er.module.topic.TopicReplyItemDelegate;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.SimpleInfo;
import me.ghui.v2er.network.bean.ThxReplyInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 05/05/2017.
 */

@Module
public class TopicModule {

    private TopicActivity mView;

    public TopicModule(TopicActivity view) {
        mView = view;
    }

    @Provides
    public LoadMoreRecyclerView.Adapter provideAdapter() {
        LoadMoreRecyclerView.Adapter adapter = new LoadMoreRecyclerView.Adapter(mView) {
            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> {
                            String userName;
                            if (v.getId() == R.id.avatar_img || v.getId() == R.id.user_name_tv) {
                                userName = ((TopicInfo.HeaderInfo) getItem(holder.index())).getUserName();
                            } else {
                                userName = ((TopicInfo.Reply) getItem(holder.index())).getUserName();
                            }
                            UserHomeActivity.open(userName, mContext);
                        },
                        R.id.avatar_img, R.id.user_name_tv, R.id.reply_avatar_img, R.id.reply_user_name_tv);

                holder.setOnClickListener(v ->
                        NodeTopicActivity.open(((TopicInfo.HeaderInfo) getItem(holder.index())).getTagLink(), mContext), R.id.tagview);
                holder.setOnClickListener(v -> {
                    TopicInfo.Reply replyInfo = (TopicInfo.Reply) getItem(holder.index());
                    if (replyInfo.hadThanked()) {
                        Toast.makeText(mContext, R.string.already_thx_cannot_return, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TopicInfo.HeaderInfo headerInfo = (TopicInfo.HeaderInfo) getItem(0);
                    mView.mPresenter.thxReplier(replyInfo.getReplyId(), headerInfo.getT())
                            .subscribe(new GeneralConsumer<ThxReplyInfo>() {
                                @Override
                                public void onConsume(ThxReplyInfo thxReplyInfo) {
                                    if (thxReplyInfo.isValid()) {
                                        replyInfo.updateThanks(true);
                                        notifyItemChanged(holder.index());
                                    } else {
                                        Utils.toast("发送感谢遇到问题");
                                    }
                                }
                            });
                }, R.id.reply_thx_img);
            }
        };
        adapter.addItemViewDelegate(new TopicHeaderItemDelegate(mView));
        adapter.addItemViewDelegate(new TopicReplyItemDelegate(mView));
        return adapter;
    }

    @Provides
    @PerActivity
    public TopicContract.IPresenter providePresenter() {
        return new TopicPresenter(mView);
    }

}
