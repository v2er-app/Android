package me.ghui.v2er.module.node;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
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
import me.ghui.v2er.network.bean.NodeTopicInfo;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.dialog.ConfirmDialog;
import me.ghui.v2er.widget.listener.AppBarStateChangeListener;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 25/05/2017.
 * 节点话题页
 */

public class NodeTopicActivity extends BaseActivity<NodeTopicContract.IPresenter> implements NodeTopicContract.IView,
        MultiItemTypeAdapter.OnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener {
    private static final String TAG_NODE_ID_KEY = KEY("node_id_key");
    private static final String TAG_INIT_PAGE_KEY = KEY("node_init_page_key");
    private static final String TAG_BASIC_NODE_INFO = KEY("node_basic_node_info");
    private String mTagName;
    //page value when enter
    private int mInitPage;

    @BindView(R.id.base_recyclerview)
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
    BaseToolBar mToolbar;
    @BindView(R.id.node_topic_num)
    TextView mNodeTopicNumTv;
    @BindView(R.id.node_star_num)
    TextView mNodeStarNumTv;
    @BindView(R.id.node_info_star_ct)
    CheckedTextView mStarBtn;

    @Inject
    LoadMoreRecyclerView.Adapter<NodeTopicInfo.Item> mAdapter;
    private NodeInfo mNodeInfo;

    private MenuItem mLoveMenuItem;
    private NodeTopicInfo mNodeTopicInfo;

    private RequestListener mGlideDownLoadListener = new RequestListener<String, GlideDrawable>() {

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            scheduleStartPostponedTransition(mNodeImg);
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            scheduleStartPostponedTransition(mNodeImg);
            return false;
        }
    };

    public static void openById(String nodeId, int page, Context context, View sourceView, NodeInfo nodeInfo) {
        if (sourceView != null && sourceView instanceof ImageView) {
            ImageView imgview = (ImageView) sourceView;
            if (imgview.getDrawable() == null) {
                sourceView = null;
            }
        }
        Navigator.from(context)
                .to(NodeTopicActivity.class)
                .putExtra(NodeTopicActivity.TAG_NODE_ID_KEY, nodeId)
                .putExtra(NodeTopicActivity.TAG_INIT_PAGE_KEY, page)
                .putExtra(NodeTopicActivity.TAG_BASIC_NODE_INFO, nodeInfo)
                .shareElement(sourceView)
                .start();
    }

    public static void open(String link, int page, Context context) {
        openById(UriUtils.getLastSegment(link), page, context, null, null);
    }

    public static void open(String link, Context context) {
        openById(UriUtils.getLastSegment(link), 1, context, null, null);
    }

    public static void open(String link, Context context, View sourceView, NodeInfo nodeInfo) {
        openById(UriUtils.getLastSegment(link), 1, context, sourceView, nodeInfo);
    }

    @Override
    protected void configSystemBars(Window window) {
        Utils.transparentBars(getWindow(), Color.TRANSPARENT, getResources().getColor(R.color.transparent_navbar_color));
    }

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
        mTagName = intent.getStringExtra(TAG_NODE_ID_KEY);
        mInitPage = intent.getIntExtra(TAG_INIT_PAGE_KEY, 1);
        mNodeInfo = (NodeInfo) intent.getSerializableExtra(TAG_BASIC_NODE_INFO);
    }

    @Override
    protected void startInject() {
        DaggerNodeTopicComponnet.builder()
                .appComponent(getAppComponent())
                .nodeTopicModule(new NodeTopicModule(this))
                .build().inject(this);
    }

    @Override
    protected boolean supportShareElement() {
        return true;
    }

    @Override
    protected void init() {
        Utils.setPaddingForStatusBar(mToolbar);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.inflateMenu(R.menu.node_info_toolbar_menu);
        mLoveMenuItem = mToolbar.getMenu().findItem(R.id.action_star);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_star) {
                onStarBtnClicked();
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

        if (mNodeInfo != null) {
            fillHeaderView(mNodeInfo);
        }
    }

    @Override
    public String nodeName() {
        return mTagName;
    }

    @Override
    public int initPage() {
        return mInitPage;
    }


    @Override
    public void fillHeaderView(NodeInfo nodeInfo) {
        mNodeInfo = nodeInfo;
        mStarBtn.setVisibility(View.VISIBLE);
        mCollapsingToolbarLayout.setTitle(nodeInfo.getTitle());
        mNodeText.setText(nodeInfo.getTitle());
        String desc = nodeInfo.getHeader();
        RichText.from(desc).into(mNodeDesTv);
        mNodeTopicNumTv.setText(mNodeInfo.getTopics() + " 个主题");
        mNodeStarNumTv.setText(mNodeInfo.getStars() + " 个收藏");
        if (mNodeImg.getDrawable() == null) {
            Glide.with(this)
                    .load(nodeInfo.getAvatar())
                    .listener(mGlideDownLoadListener)
                    .into(mNodeImg);
            Glide.with(this)
                    .load(nodeInfo.getAvatar())
                    .bitmapTransform(new BlurTransformation(this))
                    .into(mBigImgBg);
        }
    }

    @Override
    public void fillListView(NodeTopicInfo nodeTopicInfo, boolean isLoadMore) {
        mNodeTopicInfo = nodeTopicInfo;
        if (mNodeTopicInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(mNodeTopicInfo.getItems(), isLoadMore);
        mRecyclerView.setHasMore(mNodeTopicInfo.getTotal() > mAdapter.getContentItemCount());
        toggleStar(mNodeTopicInfo.hasStared());
    }


    @OnClick(R.id.node_info_star_ct)
    void onStarBtnClicked() {
        //star or unstar
        if (mNodeTopicInfo.hasStared()) {
            new ConfirmDialog.Builder(getActivity())
                    .title("取消收藏节点")
                    .msg("确定取消收藏节点吗？")
                    .positiveText(R.string.ok, dialog -> mPresenter.starNode(mNodeTopicInfo.getFavoriteLink()))
                    .negativeText(R.string.cancel)
                    .build().show();
        } else {
            mPresenter.starNode(mNodeTopicInfo.getFavoriteLink());
        }
    }

    @Override
    public void afterStarNode() {
        toast("收藏成功");
        toggleStar(true);
        mNodeTopicInfo.updateStarStatus(true);
    }

    @Override
    public void afterUnStarNode() {
        toast("取消收藏成功");
        toggleStar(false);
        mNodeTopicInfo.updateStarStatus(false);
    }

    private void toggleStar(boolean isStared) {
        mLoveMenuItem.setIcon(isStared ?
                R.drawable.ic_star_selected : R.drawable.ic_star_normal);
        mStarBtn.setChecked(isStared);
        mStarBtn.setText(isStared ? "已收藏" : "收藏");
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        NodeTopicInfo.Item item = mAdapter.getItem(position);
        TopicBasicInfo basicInfo = new TopicBasicInfo.Builder(item.getTitle(), item.getAvatar())
                .commentNum(item.getCommentNum())
                .author(item.getUserName())
                .tag(mNodeInfo.getTitle())
                .build();
        // TODO: 14/07/2017 topicLink is null
        TopicActivity.open(mAdapter.getDatas().get(position).getTopicLink(), this, holder.getView(R.id.title_tv), basicInfo);
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadData(willLoadPage);
    }
}
