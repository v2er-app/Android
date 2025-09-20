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
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import android.util.TypedValue;

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

                // Apply dynamic font sizing
                TextView titleTv = holder.getTextView(R.id.title_tv);
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getTitleSize());
                titleTv.setText(item.getTitle());

                TextView userNameTv = holder.getTextView(R.id.user_name_tv);
                userNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                userNameTv.setText(item.getUserName());

                TextView clickCountTv = holder.getTextView(R.id.click_count_tv);
                clickCountTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                clickCountTv.setText("点击" + item.getClickNum());

                TextView commentTV = holder.getTextView(R.id.comment_num_tv);
                commentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
                commentTV.setText("评论" + item.getCommentNum());
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
