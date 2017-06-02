package me.ghui.v2ex.injector.module;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonLoadMoreAdapter;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.module.node.NodeTopicActivity;
import me.ghui.v2ex.module.node.NodeTopicContract;
import me.ghui.v2ex.module.node.NodeTopicPresenter;
import me.ghui.v2ex.module.user.UserHomeActivity;
import me.ghui.v2ex.network.bean.NodesInfo;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

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
    public LoadMoreRecyclerView.Adapter<NodesInfo.Item> provideAdapter() {
        return new CommonLoadMoreAdapter<NodesInfo.Item>(mActivity, R.layout.node_topic_item) {
            @Override
            protected void convert(ViewHolder holder, NodesInfo.Item item, int position) {
                Glide.with(mContext)
                        .load(item.getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, item.getUserName());
                holder.setText(R.id.title_tv, item.getTitle());
                holder.setText(R.id.click_count_tv, "点击" + item.getClickNum());
                holder.setText(R.id.comment_num_tv, "评论" + item.getCommentNum());
            }

            @Override
            protected void bindListener(ViewHolder holder, int viewType) {
                super.bindListener(holder, viewType);
                holder.setOnClickListener(v -> {
                    String username = getItem(holder.getAdapterPosition()).getUserName();
                    UserHomeActivity.open(username, mContext);
                }, R.id.user_name_tv, R.id.avatar_img);
            }
        };
    }

    @Provides
    public NodeTopicContract.IPresenter providePresenter() {
        return new NodeTopicPresenter(mActivity);
    }
}
