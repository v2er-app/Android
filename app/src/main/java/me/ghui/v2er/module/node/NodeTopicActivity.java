package me.ghui.v2er.module.node;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zzhoujay.richtext.RichText;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.injector.component.DaggerNodeTopicComponnet;
import me.ghui.v2er.injector.module.NodeTopicModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.NodeInfo;
import me.ghui.v2er.network.bean.NodesInfo;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.listener.AppBarStateChangeListener;

/**
 * Created by ghui on 25/05/2017.
 * 节点话题页
 */

// TODO: 25/05/2017
public class NodeTopicActivity extends BaseActivity<NodeTopicContract.IPresenter> implements NodeTopicContract.IView,
        MultiItemTypeAdapter.OnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener {
    private static final String TAG_NODE_ID_KEY = KEY("node_id_key");
    private static final String TAG_INIT_PAGE_KEY = KEY("node_init_page_key");
    private String mTagId;
    //page value when enter
    private int mInitPage;

    @BindView(R.id.common_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.node_img)
    ImageView mNodeImg;
    @BindView(R.id.big_img_bg)
    ImageView mBigImgBg;
    @BindView(R.id.node_name_tv)
    TextView mNodeText;
    @BindView(R.id.node_describtion_tv)
    TextView mNodeDesTv;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.node_info_appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.node_info_toobar)
    Toolbar mToolbar;
    @BindView(R.id.node_topic_num)
    TextView mNodeTopicNumTv;
    @BindView(R.id.node_star_num)
    TextView mNodeStarNumTv;
    @BindView(R.id.node_info_star_ct)
    CheckedTextView mStarBtn;

    @Inject
    LoadMoreRecyclerView.Adapter<NodesInfo.Item> mAdapter;
    private NodeInfo mNodeInfo;

    private MenuItem mLoveMenuItem;

    public static void openById(String nodeId, int page, Context context) {
        Navigator.from(context)
                .to(NodeTopicActivity.class)
                .putExtra(NodeTopicActivity.TAG_NODE_ID_KEY, nodeId)
                .putExtra(NodeTopicActivity.TAG_INIT_PAGE_KEY, page)
                .start();
    }

    public static void open(String link, int page, Context context) {
        openById(UriUtils.getLastSegment(link), page, context);
    }

    public static void open(String link, Context context) {
        openById(UriUtils.getLastSegment(link), 1, context);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_tag_page;
    }

    @Override
    protected Toolbar attachToolbar() {
        return null;
    }

    @Override
    protected void parseExtras(Intent intent) {
        mTagId = intent.getStringExtra(TAG_NODE_ID_KEY);
        mInitPage = intent.getIntExtra(TAG_INIT_PAGE_KEY, 1);
    }

    @Override
    protected void startInject() {
        DaggerNodeTopicComponnet.builder()
                .appComponent(getAppComponent())
                .nodeTopicModule(new NodeTopicModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        mToolbar.inflateMenu(R.menu.node_info_toolbar_menu);
        mLoveMenuItem = mToolbar.getMenu().findItem(R.id.action_star);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_star) {
                // TODO: 01/06/2017  
                Toast.makeText(getContext(), "do Star...", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        mRecyclerView.setOnLoadMoreListener(this);
        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    mToolbar.setTitle(null);
                    mToolbar.setSubtitle(null);
                    mLoveMenuItem.setVisible(false);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    mLoveMenuItem.setVisible(true);
                    if (mNodeInfo != null) {
                        mToolbar.setTitle(mNodeInfo.getTitle());
                        mToolbar.setSubtitle(mNodeInfo.getTopics() + " 个主题");
                    }
                } else {
                    //中间状态
                }
            }
        });
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
    public int initPage() {
        return mInitPage;
    }

    @Override
    public void fillHeaderView(NodeInfo nodeInfo) {
        if (nodeInfo == null) return;
        mNodeInfo = nodeInfo;
        mCollapsingToolbarLayout.setTitle(nodeInfo.getTitle());
        mNodeText.setText(nodeInfo.getTitle());
        RichText.fromHtml(nodeInfo.getHeader()).into(mNodeDesTv);
        mNodeTopicNumTv.setText(mNodeInfo.getTopics() + " 个主题");
        mNodeStarNumTv.setText(mNodeInfo.getStars() + " 个收藏");
        Glide.with(this)
                .load(nodeInfo.getAvatar())
                .into(mNodeImg);
        Glide.with(this).load(nodeInfo.getAvatar())
                .bitmapTransform(new BlurTransformation(this))
                .into(mBigImgBg);
    }

    @Override
    public void fillListView(NodesInfo nodesInfo, boolean isLoadMore) {
        if (nodesInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(nodesInfo.getItems(), isLoadMore);
        // TODO: 03/06/2017 check page 
        mRecyclerView.setHasMore(nodesInfo.getTotal() > mAdapter.getContentItemCount());
        toggleStar(nodesInfo.hasStared());
    }

    private void toggleStar(boolean isStared) {
        mLoveMenuItem.setIcon(isStared ?
                R.drawable.ic_star_selected : R.drawable.ic_star_normal);
        mStarBtn.setChecked(isStared);
        mStarBtn.setText(isStared ? "已收藏" : "收藏");
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        TopicActivity.open(mAdapter.getDatas().get(position).getTopicLink(), this);
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadData(willLoadPage);
    }
}
