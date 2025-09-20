package me.ghui.v2er.injector.module;

import androidx.annotation.Nullable;
import android.widget.TextView;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.module.user.UserHomeContract;
import me.ghui.v2er.module.user.UserHomePresenter;
import me.ghui.v2er.network.bean.UserPageInfo;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.widget.richtext.RichText;
import android.util.TypedValue;

/**
 * Created by ghui on 01/06/2017.
 */

@Module
public class UserHomeModule {

    private UserHomeActivity mActivity;

    public UserHomeModule(UserHomeActivity activity) {
        mActivity = activity;
    }

    @Provides
    public UserHomeContract.IPresenter providePresenter() {
        return new UserHomePresenter(mActivity);
    }

    @Provides
    public MultiItemTypeAdapter<UserPageInfo.Item> provideAdapter() {
        MultiItemTypeAdapter<UserPageInfo.Item> multiItemTypeAdapter = new MultiItemTypeAdapter<UserPageInfo.Item>(mActivity) {
            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v ->
                        NodeTopicActivity.open(((UserPageInfo.TopicItem) getItem(holder.index())).getTagLink(), mContext), R.id.tagview);

            }
        };
        multiItemTypeAdapter.addItemViewDelegate(new ItemViewDelegate<UserPageInfo.Item>(mActivity) {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.user_page_topic_list_item;
            }

            @Override
            public boolean isForViewType(@Nullable UserPageInfo.Item item, int position) {
                return item instanceof UserPageInfo.TopicItem;
            }

            @Override
            public void convert(ViewHolder holder, UserPageInfo.Item item, int position) {
                UserPageInfo.TopicItem topicItem = (UserPageInfo.TopicItem) item;

                // Apply dynamic font sizing
                TextView titleTv = holder.getTextView(R.id.title_tv);
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getTitleSize());
                titleTv.setText(topicItem.getTitle());

                TextView userNameTv = holder.getTextView(R.id.user_name_tv);
                userNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                userNameTv.setText(topicItem.getUserName());

                TextView timeTv = holder.getTextView(R.id.time_tv);
                timeTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                timeTv.setText(topicItem.getTime());

                TextView tagTv = holder.getTextView(R.id.tagview);
                tagTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                tagTv.setText(topicItem.getTag());

                TextView commentTV = holder.getTextView(R.id.comment_num_tv);
                commentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                commentTV.setText("评论" + topicItem.getReplyNum());
                ViewUtils.highlightCommentNum(commentTV);
            }
        });

        multiItemTypeAdapter.addItemViewDelegate(new ItemViewDelegate<UserPageInfo.Item>(mActivity) {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.user_home_page_reply_item;
            }

            @Override
            public boolean isForViewType(@Nullable UserPageInfo.Item item, int position) {
                return item instanceof UserPageInfo.ReplyItem;
            }

            @Override
            public void convert(ViewHolder holder, UserPageInfo.Item item, int position) {
                UserPageInfo.ReplyItem replyItem = (UserPageInfo.ReplyItem) item;

                // Apply dynamic font sizing
                TextView titleTv = holder.getTextView(R.id.reply_title_tv);
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getTitleSize());
                titleTv.setText(replyItem.getTitle());

                TextView contentTv = holder.getTextView(R.id.reply_content_tv);
                contentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getContentSize());
                RichText.from(replyItem.getContent())
                        .widthDelta(43)
                        .into(contentTv);

                TextView timeTv = holder.getTextView(R.id.reply_time_tv);
                timeTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                timeTv.setText(replyItem.getTime());
            }
        });

        return multiItemTypeAdapter;
    }
}
