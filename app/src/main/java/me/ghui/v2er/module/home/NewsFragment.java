package me.ghui.v2er.module.home;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.OnFragmentReEnter;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.injector.component.DaggerNewsComponent;
import me.ghui.v2er.injector.module.NewsModule;
import me.ghui.v2er.module.base.BaseFragment;
import me.ghui.v2er.module.create.CreateTopicActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.NewsInfo;
import me.ghui.v2er.network.bean.TopicBasicInfo;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseRecyclerView;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 22/03/2017.
 */

public class NewsFragment extends BaseFragment<NewsContract.IPresenter> implements NewsContract.IView,
        MultiItemTypeAdapter.OnItemClickListener, OnFragmentReEnter, MainActivity.OnNewsTabClickListener {

    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mRecyclerView;
    @BindView(R.id.news_fab_btn)
    FloatingActionButton mNewFab;
    @BindView(R.id.tabs_recyclerview)
    BaseRecyclerView mTabsRecyclerView;
    @BindView(R.id.tabs_wrapper)
    View mTabsWrapper;
    private TabInfo mCurrentTab;

    @Inject
    LoadMoreRecyclerView.Adapter<NewsInfo.Item> mAdapter;
    @Inject
    CommonAdapter<TabInfo> mTabAdapter;

    private boolean mNeedHideFab = false;

    private UpdateUnReadMsgDelegate mUpdateUnReadMsgDelegate;

    public void setUpdateUnReadMsgDelegate(UpdateUnReadMsgDelegate updateUnReadMsgDelegate) {
        mUpdateUnReadMsgDelegate = updateUnReadMsgDelegate;
    }

    public static NewsFragment newInstance() {
        Bundle args = new Bundle();
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.frag_news;
    }

    @Override
    protected void startInject() {
        DaggerNewsComponent.builder()
                .appComponent(getAppComponent())
                .newsModule(new NewsModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {
        mCurrentTab = TabInfo.getSelectTab();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(willLoadPage -> {
            Logger.e("onLoadMore.willLoadPage: " + willLoadPage);

            if (!mCurrentTab.isDefaultTab()) {
                toast("只有全部标签支持加载更多");
                mRecyclerView.setHasMore(false);
                return;
            }

            if (!UserUtils.isLogin()) {
                toast("登录后才能加载更多");
                mRecyclerView.setHasMore(false);
                return;
            }

            mPresenter.loadMore(willLoadPage);
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                    mNewFab.hide(); // or hideFab(), see below
                else if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    showFab(true);
            }
        });

        mTabsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mTabsRecyclerView.setAdapter(mTabAdapter);
        mTabAdapter.setOnItemClickListener((view, holder, position) -> {
            mCurrentTab = TabInfo.getDefault().get(position);
            TabInfo.saveSelectTab(mCurrentTab);
            MainActivity activity = (MainActivity) getActivity();
            activity.getTabView(0).setText(mCurrentTab.title);
            hideTabs();
            getPtrLayout().autoRefresh();
//            mPresenter.start();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFabBtn();
    }

    private void updateFabBtn() {
        mNeedHideFab = Pref.readBool(R.string.pref_key_hide_create_btn);
        if (mNeedHideFab) {
            mNewFab.hide();
            return;
        }
        mNewFab.show();
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mNewFab.getLayoutParams();
        layoutParams.bottomMargin = ScaleUtils.dp(20) + Utils.getNavigationBarHeight();
    }

    @Override
    protected PtrHandler attachPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mRecyclerView.resetWillLoadPage();
                mPresenter.start();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, mRecyclerView, header);
            }
        };
    }

    @Override
    public void fillView(NewsInfo newsInfos, boolean isLoadMore) {
        if (mUpdateUnReadMsgDelegate != null) {
            mUpdateUnReadMsgDelegate.updateUnReadMsg(1, newsInfos.getUnReadCount());
        }

        List<NewsInfo.Item> items = newsInfos.getItems();
        mAdapter.setData(items, isLoadMore);
        mRecyclerView.setHasMore(true);
        if (!isLoadMore) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public TabInfo getCurrentTab() {
        return mCurrentTab;
    }

    @OnClick(R.id.news_fab_btn)
    void onNewFabClicked() {
        if (PreConditions.notLoginAndProcessToLogin(getContext())) return;
        Navigator.from(getContext()).to(CreateTopicActivity.class).start();
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
//        mNewFab.setVisibility(View.INVISIBLE);
        View shareView = holder.getView(R.id.avatar_img);
        NewsInfo.Item item = mAdapter.getDatas().get(position);
        TopicBasicInfo basicInfo = new TopicBasicInfo.Builder(item.getTitle(), item.getAvatar())
                .author(item.getUserName())
                .tag(item.getTagName())
                .commentNum(item.getReplies())
                .build();
        TopicActivity.open(item.getLinkPath(),
                getContext(), shareView, basicInfo);
    }

    @Override
    public void onFragmentReEnter() {
        showFab(true);
    }

    private void showFab(boolean shouldAnimate) {
        if (mNeedHideFab) return;
        if (shouldAnimate) mNewFab.show();
        else mNewFab.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tabs_wrapper)
    void onTabsWrapperBgClicked() {
        mTabsWrapper.setVisibility(View.GONE);
    }

    @Override
    public void onNewsTabClicked() {
        // TODO: 22/08/2017 show or hide tab choose menu
        if (mTabsWrapper.getVisibility() != View.VISIBLE) {
            mTabsWrapper.setVisibility(View.VISIBLE);
        } else {
            mTabsWrapper.setVisibility(View.GONE);
        }
    }

    private void showTabs() {
        mTabsWrapper.setVisibility(View.VISIBLE);
    }

    private void hideTabs() {
        mTabsWrapper.setVisibility(View.GONE);
    }

}
