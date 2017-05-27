package me.ghui.v2ex.module.node;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.injector.component.DaggerNodeTopicComponnet;
import me.ghui.v2ex.injector.module.NodeTopicModule;
import me.ghui.v2ex.module.base.BaseActivity;
import me.ghui.v2ex.module.topic.TopicActivity;
import me.ghui.v2ex.network.bean.NodeInfo;
import me.ghui.v2ex.network.bean.NodesInfo;
import me.ghui.v2ex.widget.BaseToolBar;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 25/05/2017.
 * 节点话题页
 */

// TODO: 25/05/2017
public class NodeTopicActivity extends BaseActivity<NodeTopicContract.IPresenter> implements NodeTopicContract.IView,
        MultiItemTypeAdapter.OnItemClickListener {

    public static final String TAG_LINK_KEY = KEY("tag_link_key");

    private String mTagId;
    private String mTagLink;

    @BindView(R.id.common_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.node_img)
    ImageView mNodeImg;
    @BindView(R.id.node_name_tv)
    TextView mNodeText;
    @BindView(R.id.node_describtion_tv)
    TextView mNodeDesTv;

    @Inject
    LoadMoreRecyclerView.Adapter<NodesInfo.Item> mAdapter;

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_tag_page;
    }

    @Override
    protected BaseToolBar attachToolbar() {
        return null;
    }

    @Override
    protected void parseExtras(Intent intent) {
        mTagLink = intent.getStringExtra(TAG_LINK_KEY);
        mTagId = mTagLink.substring(mTagLink.lastIndexOf("/") + 1);
    }

    @Override
    protected void startInject() {
        DaggerNodeTopicComponnet.builder()
                .appComponent(getAppComponent())
                .nodeTopicModule(new NodeTopicModule(this))
                .build().inject(this);
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        toolBar.setElevation(0);
    }

    @Override
    protected void init() {
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);


        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void autoLoad() {
        mPresenter.start();
    }

    @Override
    public String nodeName() {
        return mTagId;
    }

    @Override
    public void fillHeaderView(NodeInfo nodeInfo) {
        if (nodeInfo == null) return;
        mNodeText.setText(nodeInfo.getTitle());
        mNodeDesTv.setText(nodeInfo.getHeader());
        Glide.with(this)
                .load(nodeInfo.getAvatar())
                .into(mNodeImg);
    }

    @Override
    public void fillListView(NodesInfo nodesInfo, boolean isLoadMore) {
        if (nodesInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(nodesInfo.getItems(), isLoadMore);
        mRecyclerView.setHasMore(nodesInfo.getTotal() > mAdapter.getContentItemCount());
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        String link = mAdapter.getDatas().get(position).getTopicLink();
        Navigator.from(getContext())
                .to(TopicActivity.class)
                .putExtra(TopicActivity.TOPIC_LINK_KEY, link)
                .start();
    }
}
