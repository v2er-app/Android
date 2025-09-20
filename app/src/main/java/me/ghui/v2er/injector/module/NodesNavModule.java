package me.ghui.v2er.injector.module;

import dagger.Module;
import dagger.Provides;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.module.home.NodesNavConstract;
import me.ghui.v2er.module.home.NodesNavFragment;
import me.ghui.v2er.module.home.NodesNavPresenter;
import me.ghui.v2er.network.bean.NodesNavInfo;
import me.ghui.v2er.util.ViewHolderFontHelper;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.widget.NavNodesWrapper;

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
                // Apply font sizing for category title
                ViewHolderFontHelper.setTextWithSize(holder, R.id.node_nav_category_tv,
                        item.getCategory(), FontSizeUtil.getTitleSize());

                NavNodesWrapper nodesWrapper = holder.getView(R.id.nodes_nav_node_wrapper);
                nodesWrapper.setData(item.getNodes());
                // The NavNodesWrapper should handle its own font sizing internally
            }
        };
    }

    @Provides
    public NodesNavConstract.IPresenter providePresenter() {
        return new NodesNavPresenter(mNavFragment);
    }

}
