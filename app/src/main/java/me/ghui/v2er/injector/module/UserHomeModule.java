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
import me.ghui.v2er.util.ViewHolderFontHelper;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.widget.richtext.RichText;

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

                // Use centralized font helper
                ViewHolderFontHelper.applyCommonListItemFonts(holder,
                        topicItem.getTitle(),
                        topicItem.getUserName(),
                        topicItem.getTime(),
                        topicItem.getTag(),
                        "评论" + topicItem.getReplyNum());
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

                // Use helper for cleaner code
                ViewHolderFontHelper.setTextWithSize(holder, R.id.reply_title_tv,
                        replyItem.getTitle(), FontSizeUtil.getTitleSize());

                ViewHolderFontHelper.setTextWithSize(holder, R.id.reply_content_tv,
                        "", FontSizeUtil.getContentSize());
                RichText.from(replyItem.getContent())
                        .widthDelta(43)
                        .into(holder.getTextView(R.id.reply_content_tv));

                ViewHolderFontHelper.setTextWithSize(holder, R.id.reply_time_tv,
                        replyItem.getTime(), FontSizeUtil.getSubTextSize());
            }
        });

        return multiItemTypeAdapter;
    }
}
