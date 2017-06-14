package me.ghui.v2er.module.topic;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.injector.component.DaggerTopicComponent;
import me.ghui.v2er.injector.module.TopicModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.widget.LoadMoreRecyclerView;


/**
 * Created by ghui on 04/05/2017.
 */

public class TopicActivity extends BaseActivity<TopicContract.IPresenter> implements TopicContract.IView,
        LoadMoreRecyclerView.OnLoadMoreListener, MultiItemTypeAdapter.OnItemClickListener {
    private static final String TOPIC_ID_KEY = KEY("topic_id_key");

    @BindView(R.id.common_recyclerview)
    LoadMoreRecyclerView mLoadMoreRecyclerView;

    @Inject
    LoadMoreRecyclerView.Adapter mAdapter;
    private String mTopicId;
    private TopicInfo mTopicInfo;
    private MenuItem mLoveMenuItem;
    private MenuItem mThxMenuItem;
    private BottomSheetDialog mBottomSheetDialog;


    public static void openById(String topicId, Context context) {
        Navigator.from(context)
                .to(TopicActivity.class)
                .putExtra(TopicActivity.TOPIC_ID_KEY, topicId)
                .start();
    }

    public static void open(String link, Context context) {
        openById(UriUtils.getLastSegment(link), context);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_load_more_recyclerview;
    }

    @Override
    protected void startInject() {
        DaggerTopicComponent.builder()
                .appComponent(getAppComponent())
                .topicModule(new TopicModule(this))
                .build().inject(this);
    }

    @Override
    protected void parseExtras(Intent intent) {
        mTopicId = intent.getStringExtra(TOPIC_ID_KEY);
    }

    @Override
    protected void configToolBar(Toolbar toolBar) {
        super.configToolBar(toolBar);
        mToolbar.inflateMenu(R.menu.topic_info_toolbar_menu);
        mLoveMenuItem = mToolbar.getMenu().findItem(R.id.action_star);
        mThxMenuItem = mToolbar.getMenu().findItem(R.id.action_thx);
        mToolbar.setOnMenuItemClickListener(item -> {
            if (mTopicInfo == null) {
                toast("请等到加载完成");
                return true;
            }
            TopicInfo.HeaderInfo headerInfo = mTopicInfo.getHeaderInfo();
            switch (item.getItemId()) {
                case R.id.action_star:
                    if (headerInfo.hadStared()) {
                        mPresenter.unStarTopic(mTopicId, headerInfo.getT());
                    } else {
                        mPresenter.starTopic(mTopicId, headerInfo.getT());
                    }
                    break;
                case R.id.action_thx:
                    if (!headerInfo.hadThanked()) {
                        mPresenter.thxCreator(mTopicId, headerInfo.getT());
                    } else {
                        toast(R.string.already_thx_cannot_return);
                        return true;
                    }
                    break;
                case R.id.action_block:
                    mPresenter.ignoreTopic(mTopicId, mTopicInfo.getOnce());
                    break;
            }
            return true;
        });
    }

    @Override
    protected void init() {
        mLoadMoreRecyclerView.addDivider();
        mLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected PtrHandler attachPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mLoadMoreRecyclerView.resetWillLoadPage();
                mPresenter.loadData(mTopicId);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, mLoadMoreRecyclerView, header);
            }
        };
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.loadData(mTopicId, willLoadPage);
    }

    @Override
    public void fillView(TopicInfo topicInfo, boolean isLoadMore) {
        mTopicInfo = topicInfo;
        if (topicInfo == null) {
            mAdapter.setData(null);
            return;
        }
        mAdapter.setData(topicInfo.getItems(isLoadMore), isLoadMore);
        mLoadMoreRecyclerView.setHasMore(topicInfo.getTotalPage());
        updateStarStatus(mTopicInfo.getHeaderInfo().hadStared(), false);
        updateThxCreatorStatus(mTopicInfo.getHeaderInfo().hadThanked(), false);
    }

    private void updateStarStatus(boolean isStared, boolean needUpdateData) {
        mLoveMenuItem.setIcon(isStared ?
                R.drawable.ic_star_selected : R.drawable.ic_star_normal);
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

    @Override
    public void afterStarTopic(TopicInfo topicInfo) {
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
    public void afterThxCreator() {
        updateThxCreatorStatus(true, true);
        toast(R.string.thx_already_send);
    }

    @Override
    public void afterIgnoreTopic() {
        toast("主题已忽略");
        finish();
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        if (mBottomSheetDialog == null) {
            mBottomSheetDialog = new BottomSheetDialog(getContext());
            mBottomSheetDialog.setContentView(R.layout.topic_reply_dialog_item);
        }
        mBottomSheetDialog.show();
    }

}
