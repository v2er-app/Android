package me.ghui.v2er.module.node;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.ghui.v2er.util.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.ShareManager;
import me.ghui.v2er.general.Vtml;
import me.ghui.v2er.injector.component.DaggerNodeTopicComponnet;
import me.ghui.v2er.injector.module.NodeTopicModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.module.topic.TopicContentFragment;
import me.ghui.v2er.network.bean.NodeInfo;
import me.ghui.v2er.network.bean.NodeTopicInfo;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.ViewUtils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.FollowProgressBtn;
import me.ghui.v2er.widget.HackRecyclerView;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.dialog.ConfirmDialog;
import me.ghui.v2er.widget.listener.AppBarStateChangeListener;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 25/05/2017.
 * 节点话题页
 */

public class NodeTopicActivity extends BaseActivity<NodeTopicContract.IPresenter> implements NodeTopicContract.IView,
        MultiItemTypeAdapter.OnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener, HackRecyclerView.AppBarTracking {
    private static final String TAG_NODE_ID_KEY = KEY("node_id_key");
    private static final String TAG_INIT_PAGE_KEY = KEY("node_init_page_key");
    private static final String TAG_BASIC_NODE_INFO = KEY("node_basic_node_info");
    private static final String PAGE_DATA_NODE_KEY = KEY("page_node_data");
    private static final String PAGE_DATA_NODE_TOPIC_KEY = KEY("page_node_topic_data");
    private static final String TOPIC_PAGE_Y_POS_KEY = KEY("topic_page_y_pos_key");
    private static final String TOPIC_Y_POS_OFFSET_KEY = KEY("topic_y_pos_offset");
    private static final String TOPIC_IS_APPBAR_EXPANDED = KEY("topic_is_appbar_expanded");
    private static final String TOPIC_CURRENT_PAGE = KEY("topic_current_page");
    @BindView(R.id.base_recyclerview)
    HackRecyclerView mRecyclerView;
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
    FollowProgressBtn mStarBtn;
    // Only exists in landscape layout
    FrameLayout mTopicContentContainer;
    @Inject
    LoadMoreRecyclerView.Adapter<NodeTopicInfo.Item> mAdapter;
    private String mTagName;
    //page value when enter
    private int mInitPage;
    // Split view fields
    private boolean mIsLandscapeMode;
    private TopicContentFragment mTopicContentFragment;
    private NodeInfo mNodeInfo;

    private MenuItem mLoveMenuItem;
    private NodeTopicInfo mNodeTopicInfo;
    private boolean isAppbarExpanted;
    private boolean mIsReturning;
    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning && !isAppbarExpanted) {
                names.clear();
                sharedElements.clear();
            }
        }
    };
    //for bugfix start: https://stackoverflow.com/questions/45192654/how-to-avoid-collapsingtoolbarlayout-not-being-snapped-or-being-wobbly-when-sc
    private int mAppBarOffset;
    private boolean mAppBarIdle = false;
    //for bugfix end
    private int mAppBarMaxOffset;
    private LinearLayoutManager mLayoutManager;

    public static void openById(String nodeId, int page, Context context, View sourceView, NodeInfo nodeInfo) {
        if (sourceView != null && sourceView instanceof ImageView) {
            ImageView imgview = (ImageView) sourceView;
            if (ViewUtils.isSameImgRes(imgview, R.drawable.avatar_placeholder_drawable) || imgview.getDrawable() == null) {
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
    public void finishAfterTransition() {
        mIsReturning = true;
        super.finishAfterTransition();
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
        // Check if we're in landscape mode
        mIsLandscapeMode = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        Utils.setPaddingForStatusBar(mToolbar);
        setEnterSharedElementCallback(mCallback);
        mToolbar.setOnDoubleTapListener(this);
        mToolbar.inflateMenu(R.menu.node_info_toolbar_menu);
        mLoveMenuItem = mToolbar.getMenu().findItem(R.id.action_star);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_star) {
                onStarBtnClicked();
            } else if (item.getItemId() == R.id.action_share) {
                if (mNodeInfo == null) return false;
                String desc = mNodeInfo.getHeader();
                String title = mNodeInfo.getTitle();
//                ShareManager.shareText(title, mNodeInfo.getUrl(), this);
                ShareManager.ShareData shareData = new ShareManager.ShareData.Builder(title)
                        .content(Vtml.fromHtml(desc).toString())
                        .link(mNodeInfo.getUrl())
                        .img(mNodeInfo.getAvatar())
                        .build();
                ShareManager shareManager = new ShareManager(shareData, this);
                shareManager.showShareDialog();
            } else if (item.getItemId() == R.id.action_block) {

            }
            return true;
        });

        // Setup landscape split view if needed
        if (mIsLandscapeMode) {
            mTopicContentContainer = findViewById(R.id.topic_content_container);
            if (mTopicContentContainer != null) {
                setupSplitView();
            }
        }

        mRecyclerView.setAppBarTracking(this);
        mRecyclerView.setOnLoadMoreListener(this);
//        mRecyclerView.addDivider();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    isAppbarExpanted = true;
                    mToolbar.setTitle(null);
                    mToolbar.setSubtitle(null);
                    mLoveMenuItem.setVisible(false);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    isAppbarExpanted = false;
                    mLoveMenuItem.setVisible(true);
                    if (mNodeInfo != null) {
                        mToolbar.setTitle(mNodeInfo.getTitle());
                        mToolbar.setSubtitle(mNodeInfo.getTopics() + " 个主题");
                    }
                } else {
                    //中间状态
                }
            }

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                super.onOffsetChanged(appBarLayout, verticalOffset);
                mAppBarOffset = verticalOffset;
                mAppBarIdle = (mAppBarOffset >= 0) || (mAppBarOffset <= mAppBarMaxOffset);
            }
        });

        mAppBarLayout.post(() -> mAppBarMaxOffset = -mAppBarLayout.getTotalScrollRange());

        if (mNodeInfo != null) {
            fillHeaderView(mNodeInfo);
        }

        mNodeTopicInfo = (NodeTopicInfo) getIntent().getSerializableExtra(PAGE_DATA_NODE_TOPIC_KEY);
        if (mNodeTopicInfo != null) {
            isAppbarExpanted = getIntent().getBooleanExtra(TOPIC_IS_APPBAR_EXPANDED, false);
            mAppBarLayout.setExpanded(isAppbarExpanted);
            mNodeInfo = (NodeInfo) getIntent().getSerializableExtra(PAGE_DATA_NODE_KEY);
            if (mNodeInfo != null) {
                fillHeaderView(mNodeInfo);
            }
            int page = getIntent().getIntExtra(TOPIC_CURRENT_PAGE, 1);
            mRecyclerView.setWillLoadPage(page);
            fillListView(mNodeTopicInfo, false);
            int pos = getIntent().getIntExtra(TOPIC_PAGE_Y_POS_KEY, 0);
            int offset = getIntent().getIntExtra(TOPIC_Y_POS_OFFSET_KEY, 0);
            L.d("1findFirstCompletelyVisibleItemPosition: " + pos + ", offset: " + offset);
            post(()-> mLayoutManager.scrollToPositionWithOffset(pos, offset));
        }

    }

    @Override
    protected void reloadMode(int mode) {
        Intent intent = getIntent();
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        int offset = mRecyclerView.getChildAt(0).getTop();
        L.d("0findFirstCompletelyVisibleItemPosition: " + pos + ", top: " + offset);
        if (mNodeTopicInfo != null) {
            mNodeTopicInfo.setItems(mAdapter.getDatas());
        }
        ActivityReloader.target(this)
                .putExtra(TAG_NODE_ID_KEY, intent.getStringExtra(TAG_NODE_ID_KEY))
                .putExtra(TAG_INIT_PAGE_KEY, intent.getStringExtra(TAG_INIT_PAGE_KEY))
                .putExtra(TOPIC_CURRENT_PAGE, mPresenter.getPage())
                .putExtra(TOPIC_PAGE_Y_POS_KEY, pos)
                .putExtra(TOPIC_Y_POS_OFFSET_KEY, offset)
                .putExtra(TOPIC_IS_APPBAR_EXPANDED, isAppbarExpanted)
                .putExtra(PAGE_DATA_NODE_KEY, mNodeInfo)
                .putExtra(PAGE_DATA_NODE_TOPIC_KEY, mNodeTopicInfo)
                .reload();
    }

    @Override
    protected void autoLoad() {
        if (mNodeTopicInfo == null) {
            super.autoLoad();
        }
    }

    @Override
    public String nodeName() {
        return mTagName;
    }

    @Override
    public String nodeId() {
        if (mNodeInfo == null) return null;
        return mNodeInfo.getId() + "";
    }

    @Override
    public int initPage() {
        return mInitPage;
    }


    @Override
    public void fillHeaderView(NodeInfo nodeInfo) {
        if (nodeInfo == null) {
            toast("加载出错!");
            return;
        }
        mNodeInfo = nodeInfo;
        mStarBtn.setVisibility(View.VISIBLE);
        mCollapsingToolbarLayout.setTitle(nodeInfo.getTitle());
        mNodeText.setText(nodeInfo.getTitle());
        String desc = nodeInfo.getHeader();
        RichText.from(desc).into(mNodeDesTv);
        mNodeTopicNumTv.setText(mNodeInfo.getTopics() + " 个主题");
        mNodeStarNumTv.setText(mNodeInfo.getStars() + " 个收藏");
        if (mNodeImg.getDrawable() == null || ViewUtils.isSameImgRes(mNodeImg, R.drawable.avatar_placeholder_drawable)) {
            GlideApp.with(this)
                    .load(nodeInfo.getAvatar())
                    .placeholder(R.drawable.avatar_placeholder_drawable)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            scheduleStartPostponedTransition(mNodeImg);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            scheduleStartPostponedTransition(mNodeImg);
                            return false;
                        }
                    }).into(mNodeImg);
            GlideApp.with(this)
                    .load(nodeInfo.getAvatar())
                    .placeholder(R.drawable.avatar_placeholder_drawable)
                    .transform(new BlurTransformation())
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
        if (mNodeTopicInfo == null) {
            toast("请等待数据加载完成");
            return;
        }
        if (mNodeTopicInfo.hasStared()) {
            new ConfirmDialog.Builder(getActivity())
                    .title("取消收藏节点")
                    .msg("确定取消收藏节点吗？")
                    .positiveText(R.string.ok, dialog -> {
                        mStarBtn.startUpdate();
                        mPresenter.starNode(mNodeTopicInfo.getFavoriteLink());
                    })
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

    @Override
    public void afterIgnoreNode() {

    }

    @Override
    public void afterUnIgnoreNode() {

    }

    private void toggleStar(boolean isStared) {
        mLoveMenuItem.setIcon(isStared ?
                R.drawable.ic_star_selected : R.drawable.ic_star_normal);
        mLoveMenuItem.getIcon().setTint(Theme.getColor(R.attr.icon_tint_color, this));
        if (isStared) {
            mStarBtn.setStatus(FollowProgressBtn.FINISHED, "已收藏", R.drawable.progress_button_done_icon);
        } else {
            mStarBtn.setStatus(FollowProgressBtn.NORMAL, "收藏", R.drawable.progress_button_follow_normal_icon);
        }
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        NodeTopicInfo.Item item = mAdapter.getItem(position);
        if (item == null) {
            return;
        }
        TopicBasicInfo basicInfo = new TopicBasicInfo.Builder(item.getTitle(), item.getAvatar())
                .commentNum(item.getCommentNum())
                .author(item.getUserName())
                .tag(mNodeInfo == null ? "" : mNodeInfo.getTitle())
                .tagLink(mNodeInfo == null ? "" : mNodeInfo.getUrl())
                .build();
        
        if (mIsLandscapeMode && mTopicContentFragment != null) {
            // In landscape mode, load topic content in the right pane
            String topicId = UriUtils.getLastSegment(item.getTopicLink());
            mTopicContentFragment.loadTopic(topicId, basicInfo);
        } else {
            // In portrait mode, open TopicActivity as usual
            TopicActivity.open(mAdapter.getDatas().get(position).getTopicLink(), this, holder.getView(R.id.avatar_img), basicInfo);
        }
    }

    private void setupSplitView() {
        // Initialize empty topic content fragment
        mTopicContentFragment = TopicContentFragment.newInstance(null, null);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.topic_content_container, mTopicContentFragment)
                .commit();
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadData(willLoadPage);
    }

    @Override
    public boolean isAppBarExpanded() {
        return mAppBarOffset == 0;
    }

    @Override
    public boolean isAppBarIdle() {
        return mAppBarIdle;
    }
}
