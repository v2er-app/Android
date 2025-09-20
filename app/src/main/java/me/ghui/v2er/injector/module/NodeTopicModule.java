package me.ghui.v2er.injector.module;

import android.widget.ImageView;
import android.widget.TextView;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.module.node.NodeTopicContract;
import me.ghui.v2er.module.node.NodeTopicPresenter;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.NodeTopicInfo;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.util.ViewHolderFontHelper;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 27/05/2017.
 */

@Module
public class NodeTopicModule {

    private NodeTopicActivity mActivity;

    public NodeTopicModule(NodeTopicActivity activity) {
        mActivity = activity;
    }

    @Provides
    public LoadMoreRecyclerView.Adapter<NodeTopicInfo.Item> provideAdapter() {
        return new CommonLoadMoreAdapter<NodeTopicInfo.Item>(mActivity, R.layout.node_topic_item) {

            @Override
            protected void convert(ViewHolder holder, NodeTopicInfo.Item item, int position) {
                GlideApp.with(mContext)
                        .load(item.getAvatar())
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into((ImageView) holder.getView(R.id.avatar_img));

                // Use helper methods for cleaner code
                ViewHolderFontHelper.setTextWithSize(holder, R.id.title_tv,
                        item.getTitle(), FontSizeUtil.getTitleSize());
                ViewHolderFontHelper.setTextWithSize(holder, R.id.user_name_tv,
                        item.getUserName(), FontSizeUtil.getSubTextSize());
                ViewHolderFontHelper.setTextWithSize(holder, R.id.click_count_tv,
                        "点击" + item.getClickNum(), FontSizeUtil.getSubTextSize());

                TextView commentTV = holder.getTextView(R.id.comment_num_tv);
                ViewHolderFontHelper.setTextWithSize(holder, R.id.comment_num_tv,
                        "评论" + item.getCommentNum(), FontSizeUtil.getSubTextSize());
                ViewUtils.highlightCommentNum(commentTV);
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> {
                    NodeTopicInfo.Item item = getItem(holder.index());
                    UserHomeActivity.open(item.getUserName(), mContext, holder.getView(R.id.avatar_img), item.getAvatar());
                }, R.id.user_name_tv, R.id.avatar_img);
            }

        };
    }

    @Provides
    public NodeTopicContract.IPresenter providePresenter() {
        return new NodeTopicPresenter(mActivity);
    }
}
