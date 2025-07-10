package me.ghui.v2er.injector.module;

import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.injector.scope.PerFragment;
import me.ghui.v2er.module.drawer.star.NodeStarContract;
import me.ghui.v2er.module.drawer.star.NodeStarFragment;
import me.ghui.v2er.module.drawer.star.NodeStarPresenter;
import me.ghui.v2er.network.bean.NodeStarInfo;
import me.ghui.v2er.util.FontSizeUtil;

/**
 * Created by ghui on 18/05/2017.
 */

@Module
public class NodeStarModule {

    private NodeStarFragment mFragment;

    public NodeStarModule(NodeStarFragment fragment) {
        mFragment = fragment;
    }

    @PerFragment
    @Provides
    public CommonAdapter<NodeStarInfo.Item> provideAdapter() {
        return new CommonAdapter<NodeStarInfo.Item>(mFragment.getContext(), R.layout.node_item) {
            @Override
            protected void convert(ViewHolder holder, NodeStarInfo.Item item, int position) {
                GlideApp.with(mContext)
                        .load(item.getImg())
                        .placeholder(R.drawable.avatar_placeholder_drawable)
                        .into((ImageView) holder.getView(R.id.node_img));
                
                // Apply font size scaling to text elements
                TextView nodeNameTv = holder.getTextView(R.id.node_name);
                nodeNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getContentSize());
                nodeNameTv.setText(item.getName());
                
                TextView nodeTopicNumTv = holder.getTextView(R.id.node_topic_num);
                nodeTopicNumTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getContentSize());
                nodeTopicNumTv.setText(item.getTopicNum() + "");
            }
        };
    }

    @Provides
    public NodeStarContract.IPresenter providePresenter() {
        return new NodeStarPresenter(mFragment);
    }

}
