package me.ghui.v2ex.injector.module;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonAdapter;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.injector.scope.PerFragment;
import me.ghui.v2ex.module.drawer.star.NodeStarContract;
import me.ghui.v2ex.module.drawer.star.NodeStarFragment;
import me.ghui.v2ex.module.drawer.star.NodeStarPresenter;
import me.ghui.v2ex.network.bean.NodeStarInfo;

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
                Glide.with(mContext)
                        .load(item.getImg())
                        .into((ImageView) holder.getView(R.id.node_img));
                holder.setText(R.id.node_name, item.getName());
                holder.setText(R.id.node_topic_num, item.getTopicNum() + "");
            }
        };
    }

    @Provides
    public NodeStarContract.IPresenter providePresenter() {
        return new NodeStarPresenter(mFragment);
    }

}
