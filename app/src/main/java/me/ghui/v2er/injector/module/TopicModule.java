package me.ghui.v2er.injector.module;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.injector.scope.PerActivity;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.module.topic.TopicContentItemDelegate;
import me.ghui.v2er.module.topic.TopicContract;
import me.ghui.v2er.module.topic.TopicHeaderItemDelegate;
import me.ghui.v2er.module.topic.TopicPresenter;
import me.ghui.v2er.module.topic.TopicReplyItemDelegate;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.GeneralConsumer;
import me.ghui.v2er.network.bean.ThxResponseInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

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
    public LoadMoreRecyclerView.Adapter<TopicInfo.Item> provideAdapter() {
        LoadMoreRecyclerView.Adapter<TopicInfo.Item> adapter = new LoadMoreRecyclerView.Adapter<TopicInfo.Item>(mView) {
            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> {
                            String userName;
                            String avatar;
                            View sourceView;
                            if (v.getId() == R.id.avatar_img || v.getId() == R.id.user_name_tv) {
                                userName = ((TopicInfo.HeaderInfo) getItem(holder.index())).getUserName();
                                avatar = ((TopicInfo.HeaderInfo) getItem(holder.index())).getAvatar();
                                sourceView = holder.getImgView(R.id.avatar_img);
                            } else {
                                userName = ((TopicInfo.Reply) getItem(holder.index())).getUserName();
                                avatar = ((TopicInfo.Reply) getItem(holder.index())).getAvatar();
                                sourceView = holder.getImgView(R.id.reply_avatar_img);
                            }
                            UserHomeActivity.open(userName, mContext, sourceView, avatar);
                        },
                        R.id.avatar_img, R.id.user_name_tv, R.id.reply_avatar_img, R.id.reply_user_name_tv);

                holder.setOnClickListener(v ->
                        NodeTopicActivity.open(((TopicInfo.HeaderInfo) getItem(holder.index())).getTagLink(), mContext), R.id.tagview);
                holder.setOnClickListener(v -> {
                    if (PreConditions.notLoginAndProcessToLogin(mView.getContext())) return;
                    TopicInfo.Reply replyInfo = (TopicInfo.Reply) getItem(holder.index());
                    if (replyInfo.hadThanked()) {
                        Toast.makeText(mContext, R.string.already_thx_cannot_return, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new ConfirmDialog.Builder((Activity) mContext)
                            .title("感谢回复者")
                            .msg("确定花费10个铜币向@" + replyInfo.getUserName() + "表达感谢？")
                            .positiveText(R.string.ok, dialog -> {
                                TopicInfo.HeaderInfo headerInfo = (TopicInfo.HeaderInfo) getItem(0);
                                mView.mPresenter.thxReplier(replyInfo.getReplyId(), headerInfo.getT())
                                        .subscribe(new GeneralConsumer<ThxResponseInfo>() {
                                            @Override
                                            public void onConsume(ThxResponseInfo thxReplyInfo) {
                                                if (thxReplyInfo.isValid()) {
                                                    replyInfo.updateThanks(true);
                                                    notifyItemChanged(holder.index());
                                                } else {
                                                    Voast.show(mContext.getString(R.string.send_thx_occured_error));
                                                }
                                            }
                                        });
                            })
                            .negativeText(R.string.cancel)
                            .build().show();
                }, R.id.reply_thx_img);
            }

            @Override
            protected boolean shouldAnimate(int pos) {
                return pos > 1 && super.shouldAnimate(pos);
            }

            @Override
            protected void animateIn(View itemView, int position) {
                super.animateIn(itemView, position);
            }

            @Override
            protected void animateOut(View itemView, int position) {
                super.animateOut(itemView, position);
            }
        };
        adapter.addItemViewDelegate(new TopicHeaderItemDelegate(mView));
        adapter.addItemViewDelegate(new TopicContentItemDelegate(mView));
        adapter.addItemViewDelegate(new TopicReplyItemDelegate(mView));
        return adapter;
    }

    @Provides
    @PerActivity
    public TopicContract.IPresenter providePresenter() {
        return new TopicPresenter(mView);
    }

}
