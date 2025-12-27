package me.ghui.v2er.module.topic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import me.ghui.v2er.module.imgur.ImageUploadHelper;
import androidx.browser.customtabs.CustomTabsClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.util.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.general.ActivityReloader;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.general.ShareElementTransitionCallBack;
import me.ghui.v2er.general.ShareManager;
import me.ghui.v2er.general.Vtml;
import me.ghui.v2er.injector.component.DaggerTopicComponent;
import me.ghui.v2er.injector.module.TopicModule;
import me.ghui.v2er.module.append.AppendTopicActivity;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.user.UserHomeActivity;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.AndroidBug5497Workaround;
import me.ghui.v2er.widget.BaseRecyclerView;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.KeyboardDetectorRelativeLayout;
import me.ghui.v2er.widget.LoadMoreRecyclerView;
import me.ghui.v2er.widget.MentionedReplySheetDialog;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

import static android.view.View.VISIBLE;


/**
 * Created by ghui on 04/05/2017.
 */

public class TopicActivity extends BaseActivity<TopicContract.IPresenter> implements TopicContract.IView,
        LoadMoreRecyclerView.OnLoadMoreListener, KeyboardDetectorRelativeLayout.IKeyboardChanged, TopicReplyItemDelegate.OnMemberClickListener, HtmlView.OnHtmlRenderListener, TopicReplySortHeaderDelegate.OnSortTypeChangeListener {
    public static final String TOPIC_ID_KEY = KEY("topic_id_key");
    private static final String TOPIC_BASIC_INFO = KEY("TOPIC_BASIC_INFO");
    private static final String TOPIC_AUTO_SCROLL_REPLY = KEY("TOPIC_AUTO_SCROLL_REPLY");
    public static final String TOPIC_INTO_KEY = KEY("TOPIC_INTO_KEY");
    private static final String TOPIC_CURRENT_PAGE = KEY("TOPIC_CURRENT_PAGE");
    private static final String TOPIC_PAGE_Y_POS_KEY = KEY("TOPIC_PAGE_Y_POS_KEY");

    public boolean isNeedAutoScroll = true;
    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mLoadMoreRecyclerView;
    @BindView(R.id.topic_reply_wrapper)
    KeyboardDetectorRelativeLayout mReplyWrapper;
    @BindView(R.id.topic_inner_reply_wrapper)
    ViewGroup mReplyInnerWrapper;
    @BindView(R.id.inner_reply_wrapper_content)
    ViewGroup mReplyLayout;
    @BindView(R.id.topic_reply_et)
    EditText mReplyEt;
    @BindView(R.id.reply_fab_btn)
    FloatingActionButton mReplyFabBtn;
    @BindView(R.id.repliers_recyclerview)
    BaseRecyclerView mReplierRecyView;
    @BindView(R.id.reply_upload_image_btn)
    ImageButton mReplyUploadImageBtn;
    @Inject
    LoadMoreRecyclerView.Adapter<TopicInfo.Item> mAdapter;
    private ImageUploadHelper mImageUploadHelper;
    @Inject
    TopicModule.TopicAtAdapter mReplierAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mMentionedLinearLayoutManager;
    private String mTopicId;
    private TopicBasicInfo mTopicBasicInfo;
    private String mAutoScrollReply;
    private TopicInfo mTopicInfo;
    private MenuItem mLoveMenuItem;
    private MenuItem mThxMenuItem;
    private MenuItem mReportMenuItem;
    private MenuItem mAppendItem;
    private MenuItem mFadeItem;
    private MenuItem mStickyItem;
    private BottomSheetDialog mMenuSheetDialog;
    private OnBottomDialogItemClickListener mBottomSheetDialogItemClickListener;
    private List<TopicInfo.Item> repliersInfo;
    private boolean mNeedWaitForTransitionEnd = true;
    private boolean mIsReturning;
    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning) {
                if (mLoadMoreRecyclerView.computeVerticalScrollOffset() > getResources().getDimension(R.dimen.common_padding_size)) {
                    names.clear();
                    sharedElements.clear();
                }
            }
        }
    };
    private boolean mIsHideReplyBtn;
    private boolean mIsLogin = UserUtils.isLogin();
    private boolean mIsScanInOrder = !Pref.readBool(R.string.pref_key_is_scan_in_reverse, false);
    private TopicInfo.ReplySortType mReplySortType = TopicInfo.ReplySortType.BY_TIME;

    /**
     * @param topicId
     * @param context
     * @param sourceView     shareElement view
     * @param topicBasicInfo pre show info
     * @param scrollToReply  auto to scrollTo the reply item
     */
    public static void openById(String topicId, Context context, View sourceView, TopicBasicInfo topicBasicInfo, String scrollToReply) {
        if (sourceView == null || topicBasicInfo == null) {
            topicBasicInfo = null;
            sourceView = null;
        }

        Navigator.from(context)
                .to(TopicActivity.class)
                .putExtra(TopicActivity.TOPIC_ID_KEY, topicId)
                .putExtra(TOPIC_BASIC_INFO, topicBasicInfo)
                .putExtra(TOPIC_AUTO_SCROLL_REPLY, scrollToReply)
                .shareElement(sourceView)
                .start();
    }

    public static void openById(String topicId, Context context, View sourceView, TopicBasicInfo topicBasicInfo) {
        openById(topicId, context, sourceView, topicBasicInfo, null);
    }

    public static void open(String link, Context context) {
        open(link, context, null, null);
    }

    public static void open(String link, Context context, View sourceView, TopicBasicInfo topicBasicInfo) {
        openById(UriUtils.getLastSegment(link), context, sourceView, topicBasicInfo, null);
    }

    public static void openWithAutoScroll(String link, Context context, String autoScrollReply) {
        openById(UriUtils.getLastSegment(link), context, null, null, autoScrollReply);
    }

    @Override
    protected void autoLoad() {
        if (mTopicInfo == null) {
            super.autoLoad();
            showLoading();
        }
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_topic_info_page;
    }

    @Override
    protected void startInject() {
        DaggerTopicComponent.builder()
                .appComponent(getAppComponent())
                .topicModule(new TopicModule(this))
                .build().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        CustomTabsClient.connectAndInitialize(this, "com.android.chrome");
    }

    @Override
    protected void parseExtras(Intent intent) {
        mTopicId = intent.getStringExtra(TOPIC_ID_KEY);
        mTopicBasicInfo = (TopicBasicInfo) intent.getSerializableExtra(TOPIC_BASIC_INFO);
        mTopicInfo = (TopicInfo) intent.getSerializableExtra(TOPIC_INTO_KEY);
        mAutoScrollReply = intent.getStringExtra(TOPIC_AUTO_SCROLL_REPLY);
        isNeedAutoScroll = Check.notEmpty(mAutoScrollReply);
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(toolBar);
        mToolbar.inflateMenu(R.menu.topic_info_toolbar_menu);
        Menu menu = mToolbar.getMenu();
        mLoveMenuItem = menu.findItem(R.id.action_star);
        mThxMenuItem = menu.findItem(R.id.action_thx);
        mAppendItem = menu.findItem(R.id.action_append);
        mFadeItem = menu.findItem(R.id.action_fade);
        mStickyItem = menu.findItem(R.id.action_sticky);
        mReportMenuItem = menu.findItem(R.id.action_report);
        MenuItem replyMenuItem = menu.findItem(R.id.action_reply);
        mIsHideReplyBtn = Pref.readBool(R.string.pref_key_hide_reply_btn);
        replyMenuItem.setVisible(mIsHideReplyBtn);
        MenuItem scanOrderMenuItem = menu.findItem(R.id.action_scan_order);
        scanOrderMenuItem.setTitle(mIsScanInOrder ? "顺序浏览" : "逆序浏览");
        mToolbar.setOnMenuItemClickListener(item -> {
            if (mTopicInfo == null) {
                if (item.getItemId() == R.id.action_open_in_browser) {
                    String topicLink = Utils.generateTopicLinkById(mTopicId);
                    Utils.openInBrowser(topicLink, this);
                } else {
                    toast("请等到加载完成");
                }
                return true;
            }
            TopicInfo.HeaderInfo headerInfo = mTopicInfo.getHeaderInfo();

            switch (item.getItemId()) {
                case R.id.action_star:
                    if (headerInfo.hadStared()) {
                        mPresenter.unStarTopic(mTopicId, mTopicInfo.getOnce());
                    } else {
                        mPresenter.starTopic(mTopicId, mTopicInfo.getOnce());
                    }
                    break;
                case R.id.action_append:
                    AppendTopicActivity.open(mTopicId, TopicActivity.this);
                    break;
                case R.id.action_thx:
                    if (UserUtils.notLoginAndProcessToLogin(false, this)) return false;
                    if (mTopicInfo.getHeaderInfo().isSelf()) {
                        toast("自己不能感谢自己");
                        return false;
                    }
                    if (!headerInfo.canSendThanks()) {
                        toast("感谢发送失败，可能因为您刚注册不久");
                        return true;
                    }
                    if (!headerInfo.hadThanked()) {
                        mPresenter.thxCreator(mTopicId, getOnce());
                    } else {
                        toast(R.string.already_thx_cannot_return);
                        return true;
                    }
                    break;
                case R.id.action_block:
                    if (UserUtils.notLoginAndProcessToLogin(false, this)) return false;
                    new ConfirmDialog.Builder(getActivity())
                            .msg("确定忽略此主题吗？")
                            .positiveText(R.string.ok, dialog -> mPresenter.ignoreTopic(mTopicId, mTopicInfo.getOnce()))
                            .negativeText(R.string.cancel)
                            .build().show();
                    break;
                case R.id.action_report:
                    if (UserUtils.notLoginAndProcessToLogin(false, this)) return false;
                    new ConfirmDialog.Builder(getActivity())
                            .msg("确定要举报这个主题吗？")
                            .positiveText(R.string.ok, dialog -> mPresenter.reportTopic())
                            .negativeText(R.string.cancel)
                            .build().show();
                    break;
                case R.id.action_sticky:
                    if (UserUtils.notLoginAndProcessToLogin(false, this)) return false;
                    new ConfirmDialog.Builder(getActivity())
                            .msg("你确认要将此主题置顶 10 分钟？该操作价格为 200 铜币。")
                            .positiveText(R.string.ok, dialog -> mPresenter.stickyTopic())
                            .negativeText(R.string.cancel)
                            .build().show();
                    break;
                case R.id.action_fade:
                    if (UserUtils.notLoginAndProcessToLogin(false, this)) return false;
                    new ConfirmDialog.Builder(getActivity())
                            .msg("你确认要将此主题下沉 1 天？")
                            .positiveText(R.string.ok, dialog -> mPresenter.fadeTopic())
                            .negativeText(R.string.cancel)
                            .build().show();
                    break;
                case R.id.action_share:
                    ShareManager.ShareData shareData = new ShareManager.ShareData.Builder(headerInfo.getTitle())
                            .content(Vtml.fromHtml(mTopicInfo.getContentInfo().getFormattedHtml()).toString())
                            .link(UriUtils.topicLink(mTopicId))
                            .img(headerInfo.getAvatar())
                            .build();
                    ShareManager shareManager = new ShareManager(shareData, this);
                    shareManager.showShareDialog();
                    break;
                case R.id.action_open_in_browser:
//                    Utils.copyToClipboard(this, mTopicInfo.getTopicLink());
//                    toast("链接已拷贝成功");
                    Utils.openInBrowser(mTopicInfo.getTopicLink(), this);
                    break;
                case R.id.action_reply:
                    animateEditInnerWrapper(true);
                    break;
                case R.id.action_scan_order:
                    // reload
                    mIsScanInOrder = !mIsScanInOrder;
                    scanOrderMenuItem.setTitle(mIsScanInOrder ? "顺序浏览" : "逆序浏览");
                    Pref.saveBool(R.string.pref_key_is_scan_in_reverse, !mIsScanInOrder);
                    mLoadMoreRecyclerView.setLoadOrder(mIsScanInOrder);
                    // 重新加载
                    loadFromStart();
                    showLoading();
                    break;
            }
            return true;
        });
    }


    @Override
    protected boolean supportShareElement() {
        return mTopicBasicInfo != null && Check.notEmpty(mTopicBasicInfo.getAvatar());
    }

    private void shareElementAnimation() {
        mNeedWaitForTransitionEnd = supportShareElement();
        final Transition transition = getWindow().getSharedElementEnterTransition();
        if (transition == null) {
            mNeedWaitForTransitionEnd = false;
            return;
        }
        transition.addListener(new ShareElementTransitionCallBack() {
            @Override
            public void onTransitionStart(Transition transition) {
                if (mIsReturning) {
//                    mReplyFabBtn.setVisibility(View.GONE);
                    mReplyFabBtn.hide();
                }
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                L.e("onTransitionEnd");
                mNeedWaitForTransitionEnd = false;
                if (mTopicInfo != null) {
                    delay(30, () -> fillView(mTopicInfo, mIsScanInOrder ? 1 : mTopicInfo.getTotalPage()));
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null || mTopicInfo != null) {
            mNeedWaitForTransitionEnd = false;

        }
    }

    @Override
    protected void reloadMode(int mode) {
        int pos = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        L.d("firstVisiablePos: " + pos);
        ActivityReloader.target(this)
                .putExtra(TOPIC_ID_KEY, getIntent().getStringExtra(TOPIC_ID_KEY))
                .putExtra(TOPIC_INTO_KEY, mTopicInfo)
                .putExtra(TOPIC_CURRENT_PAGE, mPresenter.getPage())
                .putExtra(TOPIC_PAGE_Y_POS_KEY, pos)
                .reload();
    }

    @Override
    protected void init() {
        AndroidBug5497Workaround.assistActivity(this);
        Utils.setPaddingForNavbar(mReplyLayout);
        setEnterSharedElementCallback(mCallback);
        setFirstLoadingDelay(300);
        shareElementAnimation();
//        mReplyFabBtn.setVisibility(!mIsLogin || mIsHideReplyBtn ? View.GONE : VISIBLE);
        if (!mIsLogin || mIsHideReplyBtn) {
            mReplyFabBtn.hide();
        } else mReplyFabBtn.show();
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mReplyFabBtn.getLayoutParams();
        layoutParams.bottomMargin = ScaleUtils.dp(20) + Utils.getNavigationBarHeight();
        mReplyWrapper.addKeyboardStateChangedListener(this);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLoadMoreRecyclerView.setLoadOrder(mIsScanInOrder);
        mLoadMoreRecyclerView.setTransitionGroup(true);
        mLoadMoreRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mLoadMoreRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int pos = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (pos == 0) {
                    //title 可见
                    mToolbar.setSubtitle(null);
                } else {
                    //title 不可见
                    if (mTopicInfo != null && mTopicInfo.getHeaderInfo() != null) {
                        mToolbar.setSubtitle(mTopicInfo.getHeaderInfo().getTitle());
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // Layer type switching removed to prevent font style changes during scrolling
                // The constant switching between LAYER_TYPE_HARDWARE and LAYER_TYPE_SOFTWARE
                // was causing font rendering inconsistencies in WebView content
            }
        });

        if (mTopicInfo != null) {
            // 代表是从夜间模式切换过来的
            mNeedWaitForTransitionEnd = false;
            int page = getIntent().getIntExtra(TOPIC_CURRENT_PAGE, 1);
            fillView(mTopicInfo, page);
            int pos = getIntent().getIntExtra(TOPIC_PAGE_Y_POS_KEY, 0);
            L.d("firstVisiablePos2: " + pos);
            post(() -> mLinearLayoutManager.scrollToPosition(pos));
        } else if (mTopicBasicInfo != null) {
            List<TopicInfo.Item> data = new ArrayList<>();
            data.add(TopicInfo.HeaderInfo.build(mTopicBasicInfo));
            mAdapter.setData(data);
            post(() -> scheduleStartPostponedTransition($(R.id.topic_header_title_tv)));
        }
        mMentionedLinearLayoutManager = new LinearLayoutManager(this);
        mReplierRecyView.setLayoutManager(mMentionedLinearLayoutManager);
        mReplierRecyView.setAdapter(mReplierAdapter);
        mReplierAdapter.setOnItemClickListener((view, holder, position) -> {
            //do fill @username
            String selectUsername = mReplierAdapter.getItem(position).getUserName();
            String inputStr = mReplyEt.getText().toString();
            int cursorPos = mReplyEt.getSelectionStart();
            String[] cuttedStrs = Utils.cutString(inputStr, cursorPos);
            if (cuttedStrs == null) return;
            if (Check.isEmpty(cuttedStrs[1])) {
                //后面无文字，append
                StringBuilder inputTextBuilder = new StringBuilder(inputStr);
                int lastIndexOfAt = inputTextBuilder.lastIndexOf("@");
                if (lastIndexOfAt > 0 && inputTextBuilder.charAt(lastIndexOfAt - 1) != ' ') {
                    inputTextBuilder.insert(lastIndexOfAt, ' ');
                }
                lastIndexOfAt = inputTextBuilder.lastIndexOf("@");
                inputTextBuilder.replace(lastIndexOfAt + 1, inputTextBuilder.length(), selectUsername + " ");
                mReplyEt.setText(inputTextBuilder);
                mReplyEt.setSelection(inputTextBuilder.length());
            } else {
                //后面有文字，insert
                int lastIndexOfAt = cuttedStrs[0].lastIndexOf("@");
                StringBuilder result = new StringBuilder(cuttedStrs[0].substring(0, lastIndexOfAt));
                String appendStr = " @" + selectUsername;
                result.append(appendStr + " " + cuttedStrs[1]);
                mReplyEt.setText(result);
                mReplyEt.setSelection(lastIndexOfAt + appendStr.length() + 1);
            }
            mReplierRecyView.setVisibility(View.GONE);
        });
        mReplyEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CharSequence changedText = s.subSequence(start, start + count);
                L.d("text: onTextChanged: " + changedText);
                if ("@".equals(changedText.toString())) {
                    int startIndex = mLinearLayoutManager.findFirstVisibleItemPosition();
                    int endIndex = mLinearLayoutManager.findLastVisibleItemPosition() + 2;
                    endIndex = Math.min(endIndex, mAdapter.getContentItemCount() - 1);
                    List<String> hintNames = new ArrayList<>(endIndex - startIndex + 1);
                    List<TopicInfo.Item> items = mAdapter.getDatas();
                    for (int i = endIndex; i >= startIndex; i--) {
                        hintNames.add(items.get(i).getUserName());
                    }
                    for (String name : hintNames) {
                        for (int i = 0; i < repliersInfo.size(); i++) {
                            TopicInfo.Item item = repliersInfo.get(i);
                            if (item.getUserName().equals(name)) {
                                repliersInfo.remove(item);
                                repliersInfo.add(0, item);
                            }
                        }
                    }
                    mReplierRecyView.setVisibility(VISIBLE);
                }
                if (mReplierRecyView.getVisibility() != VISIBLE) return;
                String inputStr = mReplyEt.getText().toString();
                int cursorPos = mReplyEt.getSelectionStart();
                int lastIndexOfat = 0;
                for (int i = cursorPos - 1; i >= 0; i--) {
                    if (inputStr.charAt(i) == '@') {
                        lastIndexOfat = i;
                        break;
                    }
                }
                L.e("lastIndexOfAt: " + lastIndexOfat);
                String text = inputStr.substring(lastIndexOfat, cursorPos);
                onInputQueryTextChanged(text);
            }
        });

        // Initialize image upload helper
        mImageUploadHelper = new ImageUploadHelper(this, new ImageUploadHelper.OnUploadListener() {
            @Override
            public void onUploadStart() {
                toast(R.string.uploading);
            }

            @Override
            public void onUploadSuccess(String imageLink) {
                insertImageLinkToReply(imageLink);
                toast(R.string.upload_success);
            }

            @Override
            public void onUploadFailed(String errorMsg) {
                toast(getString(R.string.upload_failed) + ": " + errorMsg);
            }
        });
    }

    @OnClick(R.id.reply_upload_image_btn)
    void onReplyUploadImageClicked() {
        mImageUploadHelper.pickImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mImageUploadHelper != null) {
            mImageUploadHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImageUploadHelper != null) {
            mImageUploadHelper.onDestroy();
        }
    }

    private void insertImageLinkToReply(String imageLink) {
        int cursorPos = mReplyEt.getSelectionStart();
        String currentText = mReplyEt.getText().toString();
        String prefix = (cursorPos == 0 || currentText.length() == 0 || currentText.charAt(cursorPos - 1) == '\n') ? "" : "\n";
        String suffix = "\n";
        String newText = currentText.substring(0, cursorPos) + prefix + imageLink + suffix + currentText.substring(cursorPos);
        mReplyEt.setText(newText);
        mReplyEt.setSelection(cursorPos + prefix.length() + imageLink.length() + suffix.length());
    }

    private void onInputQueryTextChanged(String query) {
        L.d("1Query: " + query);
        if (Check.isEmpty(query)) {
            mReplierRecyView.setVisibility(View.GONE);
            return;
        }
        if (Check.notEmpty(query) && query.startsWith("@")) {
            query = query.substring(1);
        }
        L.d("2Query: " + query);
        mReplierAdapter.getFilter().filter(query);
    }

    /**
     * 下拉刷新，重新加载页面
     */
    private void loadFromStart() {
        // TODO: 2019/4/17 逆序第一次加载完需要重置willLoadPage = totalPage -1
        mTopicInfo = null;
        int willLoadPage = mIsScanInOrder ? 1 : 999;
        mLoadMoreRecyclerView.setWillLoadPage(willLoadPage);
        mNeedWaitForTransitionEnd = false;
        mPresenter.loadData(mTopicId, willLoadPage);
    }


    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> loadFromStart();
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadData(mTopicId, willLoadPage);
    }

    @Override
    public boolean getScanOrder() {
        return mIsScanInOrder;
    }

    @Override
    public String getTopicId() {
        return mTopicId;
    }

    @Override
    public String getOnce() {
        if (mTopicInfo == null) return null;
        return mTopicInfo.getOnce();
    }

    @Override
    public void fillView(TopicInfo topicInfo, int page) {
        mTopicInfo = topicInfo;
        if (mNeedWaitForTransitionEnd) return;
        if (topicInfo == null) {
            mAdapter.setData(null);
            return;
        }
        // 对于逆序加载，页面第一次打开时不知道一共有多少页，自然也就不知道首先应该加载哪一页
        // 所以对于这种情况，默认要加载的页面为999，这样v2ex也会返回最后一页的内容，当内容返回
        // 后就可以拿到准确的总页面数，此时记得如果是逆序浏览要修正正确的willLoadPage
        if (page == 999) {
            // 纠正当前加载的真实页面
            page = topicInfo.getTotalPage();
            // 纠正当前加载的willLoadPage为正确值
            mLoadMoreRecyclerView.setWillLoadPage(page);
        }
        boolean hasMore = mIsScanInOrder ? page < mTopicInfo.getTotalPage() : page > 1;
        // 内部会计算下一次的willLoadPage
        mLoadMoreRecyclerView.setHasMore(hasMore);
        // TODO: 2019/4/17 reverse
        boolean isLoadMore = mIsScanInOrder ? page > 1 : page != topicInfo.getTotalPage();
        if (!mIsScanInOrder) {
            // 逆序加载的话需要reverse reply序列

        }
        // TODO: 2019-06-23 save info from adapter
        mAdapter.setData(topicInfo.getItems(isLoadMore, mIsScanInOrder, mReplySortType), isLoadMore);
        if (!topicInfo.getContentInfo().isValid()) {
            onRenderCompleted();
        }
        TopicInfo.HeaderInfo headerInfo = mTopicInfo.getHeaderInfo();
        updateStarStatus(headerInfo.hadStared(), false);
        updateThxCreatorStatus(headerInfo.hadThanked(), false);
        updateReportMenuItem(mTopicInfo.hasReportPermission(), mTopicInfo.hasReported());
        boolean isSelf = mTopicInfo.getHeaderInfo().isSelf();
        mAppendItem.setVisible(isSelf && mTopicInfo.getHeaderInfo().canAppend());
        mFadeItem.setVisible(isSelf && mTopicInfo.canfade());
        mStickyItem.setVisible(isSelf && mTopicInfo.canSticky());
        if (!mIsHideReplyBtn && mIsLogin) {
//            mReplyFabBtn.setVisibility(VISIBLE);
            mReplyFabBtn.show();
        }
        fillAtList();
        autoScroll();
    }

    private void autoScroll() {
        // TODO: 2019-08-11 向上滑动帖子调动
        if (!isNeedAutoScroll) return;
        List<TopicInfo.Reply> items = mTopicInfo.getReplies();
        if (Check.isEmpty(items)) return;
        int position = 0;
        for (int i = 0; i < items.size(); i++) {
            TopicInfo.Reply item = items.get(i);
            if (mAutoScrollReply.equals(item.getReplyContent())) {
                position = i;
                if (mTopicInfo.getContentInfo().isValid()) {
                    position += 2;
                } else {
                    position += 1;
                }
                break;
            }
        }

        if (isNeedAutoScroll && position > 0) {
            isNeedAutoScroll = false;
            int finalPosition = position;
            delay(350, () -> {
                mLinearLayoutManager.scrollToPositionWithOffset(finalPosition, 0);
                post(() -> {
//                    if (mLinearLayoutManager.findFirstVisibleItemPosition() == finalPosition)
//                        return;
                    View itemView = mLinearLayoutManager.findViewByPosition(finalPosition);
                    if (itemView == null) {
                        Voast.debug("itemView is null");
                        return;
                    }
                    itemView.startAnimation(AnimationUtils.loadAnimation(TopicActivity.this, R.anim.item_shake));
                });
            });
        }
    }

    private void fillAtList() {
        List<TopicInfo.Item> datum = mAdapter.getDatas();
        if (repliersInfo == null) {
            repliersInfo = new ArrayList<>(datum.size() - 1);
        } else {
            repliersInfo.clear();
        }

        for (TopicInfo.Item item : datum) {
            String name = item.getUserName();
            //do check...
            if (Check.notEmpty(name)) {
                boolean alreadyHas = false;
                for (TopicInfo.Item reply : repliersInfo) {
                    if (name.equals(reply.getUserName())) {
                        //already has the same username one , break
                        alreadyHas = true;
                        break;
                    }
                }
                if (!alreadyHas) {
                    repliersInfo.add(item);
                }
            }
        }
        mReplierAdapter.setData(repliersInfo);
    }

    @Override
    public List<TopicInfo.Item> topicReplyInfo() {
        return repliersInfo;
    }

    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        super.finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        if (mReplyWrapper.getVisibility() == View.VISIBLE) {
            animateEditInnerWrapper(false);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.reply_fab_btn)
    void onNewReplyFlbClicked(FloatingActionButton button) {
        if (button != null) {
            button.hide();
        }
        animateEditInnerWrapper(true);
    }

    private int getRelativeY() {
        int[] loc = new int[2];
        mReplyInnerWrapper.getLocationOnScreen(loc);
        int wrapperY = loc[1];
        mReplyFabBtn.getLocationOnScreen(loc);
        int btnY = loc[1];
        int delta = btnY - wrapperY + mReplyFabBtn.getMeasuredHeight() / 2;
        return delta;
    }

    void animateEditInnerWrapper(boolean isShow) {
        if (mSlidrInterface != null) {
            if (isShow) mSlidrInterface.lock();
            else mSlidrInterface.unlock();
        }

        int cX = ScaleUtils.getScreenW() - mReplyFabBtn.getMeasuredWidth() / 2 - ScaleUtils.dp(16);
        int startRadius = ScaleUtils.dp(25);
        int endRadius = ScaleUtils.getScreenW();
        if (isShow) {//show edit wrapper
            mReplyFabBtn.hide();
            Animator animator = ViewAnimationUtils.createCircularReveal(mReplyInnerWrapper, cX, getRelativeY(), startRadius, endRadius);
            animator.setDuration(400);
            animator.setStartDelay(100);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mReplyWrapper.setVisibility(View.VISIBLE);
                }
            });
            animator.start();
        } else {//hide wrapper
            if (mReplyWrapper.getVisibility() != View.VISIBLE) return;
            Animator animator = ViewAnimationUtils.createCircularReveal(mReplyInnerWrapper, cX, getRelativeY(), endRadius, startRadius);
            animator.setDuration(300);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mReplyWrapper.setVisibility(View.INVISIBLE);
                    if (!mIsHideReplyBtn && mIsLogin) mReplyFabBtn.show();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    Voast.debug("TopicPage onAnimationCancel");
                    onAnimationEnd(animation);
                }
            });
            animator.start();
            mReplierRecyView.setVisibility(View.GONE);
        }
    }


    private void updateStarStatus(boolean isStared, boolean needUpdateData) {
        mLoveMenuItem.setIcon(isStared ?
                R.drawable.ic_star_selected : R.drawable.ic_star_normal);
        mLoveMenuItem.getIcon().setTint(Theme.getColor(R.attr.icon_tint_color, this));
        if (needUpdateData) {
            mTopicInfo.getHeaderInfo().updateStarStatus(isStared);
        }
    }

    private void updateThxCreatorStatus(boolean thxed, boolean needUpdateData) {
        mThxMenuItem.setTitle(thxed ? getString(R.string.thx_already_send) : getString(R.string.thx_str));
        if (needUpdateData) {
            mTopicInfo.getHeaderInfo().updateThxStatus(thxed);
        }
    }

    private void updateReportMenuItem(boolean hasReportPermission, boolean hasReported) {
        mReportMenuItem.setTitle(hasReported ? "已举报" : "举报");
        mReportMenuItem.setEnabled(!hasReported);
        mReportMenuItem.setVisible(hasReportPermission);
    }


    @Override
    public void afterStarTopic(TopicInfo topicInfo) {
        if (mTopicInfo == null) {
            toast("收藏遇到问题");
            return;
        }
        mTopicInfo.getHeaderInfo().setFavoriteLink(topicInfo.getHeaderInfo().getFavoriteLink());
        updateStarStatus(mTopicInfo.getHeaderInfo().hadStared(), true);
        toast("收藏成功");
    }

    @Override
    public void afterUnStarTopic(TopicInfo topicInfo) {
        mTopicInfo.getHeaderInfo().setFavoriteLink(topicInfo.getHeaderInfo().getFavoriteLink());
        updateStarStatus(mTopicInfo.getHeaderInfo().hadStared(), true);
        toast("取消收藏成功");
    }

    @Override
    public void afterThxCreator(boolean success) {
        if (success) {
            updateThxCreatorStatus(true, true);
            toast(R.string.thx_already_send);
        } else {
            toast(getString(R.string.send_thx_occured_error));
        }
    }

    @Override
    public void afterIgnoreTopic(boolean success) {
        if (success) {
            toast("主题已忽略");
            finish();
        } else {
            toast("忽略主题遇到问题");
        }
    }

    @Override
    public void afterIgnoreReply(int position) {
        toast("已忽略");
        mAdapter.getDatas().remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void afterReportTopic(boolean success) {
        if (success) {
            toast("举报成功");
            finish();
        } else {
            toast("举报主题遇到问题");
        }
    }

    @Override
    public void afterReplyTopic(TopicInfo topicInfo) {
        if (topicInfo.isValid()) {
            toast("回复成功");
            Utils.toggleKeyboard(false, mReplyEt);
            animateEditInnerWrapper(false);
            fillView(topicInfo, topicInfo.getTotalPage());
            mReplyEt.setText(null);
        } else {
            toast("回复失败");
        }
    }

    @Override
    public void afterFadeTopic(boolean isSuccess) {
        toast(isSuccess ? "下沉成功" : "下沉失败");
    }

    @Override
    public void afterStickyTopic(boolean isSuccess) {
        toast(isSuccess ? "置顶10分钟成功" : "置顶失败");
    }


    @OnClick(R.id.reply_send_btn)
    void onPostBtnClicked() {
        CharSequence text = mReplyEt.getText();
        if (Check.isEmpty(text)) {
            toast("回复不能为空");
            return;
        }
        if (mTopicInfo == null) {
            toast("页面加载失败");
            return;
        }
        mPresenter.replyTopic(mTopicId, mTopicInfo.toReplyMap(text.toString()));
    }

    public void onItemMoreMenuClick(int position) {
        if (mMenuSheetDialog == null) {
            mMenuSheetDialog = new BottomSheetDialog(getContext());
            mMenuSheetDialog.setContentView(R.layout.topic_reply_dialog_item);
            ViewGroup parentView = mMenuSheetDialog.findViewById(R.id.topic_reply_dialog_rootview);
            mBottomSheetDialogItemClickListener = new OnBottomDialogItemClickListener();
            for (int i = 0; i < parentView.getChildCount(); i++) {
                parentView.getChildAt(i).setOnClickListener(mBottomSheetDialogItemClickListener);
            }
        }
        mBottomSheetDialogItemClickListener.setPosition(position);
        mMenuSheetDialog.show();
    }

    @Override
    public void onKeyboardShown() {
        L.d("onKeyboardShown");
        mReplyLayout.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onKeyboardHidden() {
        L.d("onKeyboardHidden");
        Utils.setPaddingForNavbar(mReplyLayout);
    }

    @Override
    public void onRenderCompleted() {
        hideLoading();
    }

    @Override
    public void onSortTypeChanged(TopicInfo.ReplySortType sortType) {
        if (mReplySortType == sortType || mTopicInfo == null) {
            return;
        }
        mReplySortType = sortType;
        // Rebuild the items list with the new sort type
        int currentPage = mIsScanInOrder ? 1 : mTopicInfo.getTotalPage();
        fillView(mTopicInfo, currentPage);
    }

    @Override
    public void onMemberClick(String userName, int index) {
        List<TopicInfo.Item> datum = mAdapter.getDatas();
        List<TopicInfo.Reply> replies = new ArrayList<>();
        if (mIsScanInOrder) {
            for (int i = index - 1; i >= 0; i--) {
                TopicInfo.Item item = datum.get(i);
                if (item instanceof TopicInfo.Reply && item.getUserName().equals(userName)) {
                    replies.add((TopicInfo.Reply) item);
                }
            }
        } else {
            for (int i = index + 1; i < datum.size(); i++) {
                TopicInfo.Item item = datum.get(i);
                if (item instanceof TopicInfo.Reply && item.getUserName().equals(userName)) {
                    replies.add((TopicInfo.Reply) item);
                }
            }
        }
        if (Check.isEmpty(replies)) return;
        MentionedReplySheetDialog mentionedReplySheetDialog = new MentionedReplySheetDialog(this);
        mentionedReplySheetDialog.setData(replies, userName);
        mentionedReplySheetDialog.show();
    }

    private class OnBottomDialogItemClickListener implements View.OnClickListener {
        private TopicInfo.Reply item;
        private int position;

        public void setPosition(int position) {
            this.position = position;
            this.item = (TopicInfo.Reply) mAdapter.getItem(position);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reply_dialog_btn1:
                    //reply to the comment
                    if (UserUtils.notLoginAndProcessToLogin(false, getContext())) return;
                    animateEditInnerWrapper(true);
                    mReplyEt.setText("@" + item.getUserName() + " ");
                    mReplyEt.setSelection(mReplyEt.getText().length());
                    Utils.toggleKeyboard(true, mReplyEt);
                    break;
                case R.id.reply_dialog_btn2:
                    //copy reply to clipboard
                    Utils.copyToClipboard(TopicActivity.this, Html.fromHtml(item.getReplyContent()).toString());
                    toast("拷贝成功");
                    break;
                case R.id.reply_dialog_btn3:
                    //ignore reply
                    if (UserUtils.notLoginAndProcessToLogin(false, getContext())) return;
                    new ConfirmDialog.Builder(getActivity())
                            .title("忽略回复")
                            .msg("确定不再显示来自@" + item.getUserName() + "的这条回复？")
                            .positiveText(R.string.ok, dialog -> mPresenter.ignoreReply(position, item.getReplyId(), mTopicInfo.getOnce()))
                            .negativeText(R.string.cancel)
                            .build().show();
                    break;
                case R.id.reply_dialog_btn4:
                    //homepage
                    UserHomeActivity.open(item.getUserName(), TopicActivity.this, null, item.getAvatar());
                    break;
            }
            mMenuSheetDialog.dismiss();
        }
    }
}
