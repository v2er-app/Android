package me.ghui.v2ex.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonAdapter;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.module.home.NodesNavConstract;
import me.ghui.v2ex.module.home.NodesNavFragment;
import me.ghui.v2ex.module.home.NodesNavPresenter;
import me.ghui.v2ex.network.bean.NodesNavInfo;
import me.ghui.v2ex.widget.NavNodesWrapper;

/**
 * Created by ghui on 22/05/2017.
 */

@Module
public class NodesNavModule {
    private NodesNavFragment mNavFragment;

    public NodesNavModule(NodesNavFragment navFragment) {
        mNavFragment = navFragment;
    }

    @Provides
    public CommonAdapter<NodesNavInfo.Item> provideAdapter() {
        return new CommonAdapter<NodesNavInfo.Item>(mNavFragment.getContext(), R.layout.nodes_nav_item) {
            @Override
            protected void convert(ViewHolder holder, NodesNavInfo.Item item, int position) {
                holder.setText(R.id.node_nav_category_tv, item.getCategory());
                NavNodesWrapper nodesWrapper = holder.getView(R.id.nodes_nav_node_wrapper);
                nodesWrapper.setData(item.getNodes());
            }
        };
    }

    @Provides
    public NodesNavConstract.IPresenter providePresenter() {
        return new NodesNavPresenter(mNavFragment);
    }

}
