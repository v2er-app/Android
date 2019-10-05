package me.ghui.v2er.module.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.flurry.android.FlurryAgent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.sentry.Sentry;
import me.ghui.toolbox.android.Check;
import me.ghui.toolbox.android.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.injector.component.DaggerSearchComponent;
import me.ghui.v2er.injector.module.SearchModule;
import me.ghui.v2er.module.base.BaseFragment;
import me.ghui.v2er.module.base.IBackable;
import me.ghui.v2er.network.UrlInterceptor;
import me.ghui.v2er.network.bean.BingSearchResultInfo;
import me.ghui.v2er.util.DayNightUtil;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseRecyclerView;
import me.ghui.v2er.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 02/06/2017.
 */

public class SearchFragment extends BaseFragment<SearchContract.IPresenter> implements SearchContract.IView,
        LoadMoreRecyclerView.OnLoadMoreListener, MultiItemTypeAdapter.OnItemClickListener, IBackable {

    @BindView(R.id.base_recyclerview)
    LoadMoreRecyclerView mResultRecyV;
    @BindView(R.id.image_search_icon)
    ImageView mSearchIcon;
    @BindView(R.id.clear_search_img)
    ImageView mClearBtn;
    @BindView(R.id.search_edit_text)
    EditText mSearchEt;
    @BindView(R.id.search_history_recyclerview)
    BaseRecyclerView mSearchHistoryRecyV;
    @BindView(R.id.search_cardview)
    CardView mCardView;
    @BindView(R.id.search_rootview)
    View mSearchRootView;

    @Inject
    LoadMoreRecyclerView.Adapter<BingSearchResultInfo.Item> mResultAdapter;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.frag_search_layout;
    }

    @Override
    protected void startInject() {
        DaggerSearchComponent.builder()
                .appComponent(getAppComponent())
                .searchModule(new SearchModule(this))
                .build().inject(this);
    }

    @Override
    protected boolean showLoadingOnCreateView() {
        return false;
    }

    @Override
    protected void init() {
        Utils.setPaddingForStatusBar(mSearchRootView);
        mCardView.setCardBackgroundColor(Theme.getColor(R.attr.dialog_bg_color, getContext()));
        mResultRecyV.addDivider(DayNightUtil.isNightMode() ? 0XFF000000 : 0XFFF5F5F5, 6);
        mResultRecyV.setLayoutManager(new LinearLayoutManager(getContext()));
        mResultRecyV.setAdapter(mResultAdapter);
        mResultRecyV.setOnLoadMoreListener(this);
        mResultAdapter.setOnItemClickListener(this);

        mSearchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mResultRecyV.getLayoutManager().scrollToPosition(0);
                mPresenter.start();
                Utils.toggleKeyboard(false, mSearchEt);
                mSearchEt.clearFocus();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCardView.getVisibility() != View.VISIBLE) {
            post(() -> animateSearchbar(true));
        }
    }

    @Override
    public void onDestroyView() {
        mCardView.setVisibility(View.GONE);
        mResultRecyV.setVisibility(View.GONE);
        super.onDestroyView();
    }

    @OnClick(R.id.image_search_icon)
    void onBackClicked() {
//        animateSearchbar(false);
    }

    @OnClick(R.id.clear_search_img)
    void onClearClicked() {
        if (Check.isEmpty(getQueryStr())) {
            animateSearchbar(false);
        } else {
            mSearchEt.setText(null);
        }
    }

    @OnClick(R.id.search_rootview)
    void onBackgroundClicked() {
        animateSearchbar(false);
    }

    @Override
    public void onBackPressed() {
        if (mCardView.getVisibility() == View.VISIBLE) {
            animateSearchbar(false);
        }
    }

    private void animateSearchbar(boolean enter) {
        Animator animator;
        if (enter) {
            animator = ViewAnimationUtils.createCircularReveal(mCardView,
                    mCardView.getWidth() - mClearBtn.getWidth() / 2,
                    mClearBtn.getWidth() / 2,
                    0, mCardView.getWidth());
            mCardView.setVisibility(View.VISIBLE);
            animator.setDuration(350);
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    Utils.toggleKeyboard(true, mSearchEt);
                }
            });
            if (mSearchRootView.isAttachedToWindow()) {
                mSearchRootView.animate().alpha(1f).start();
            } else {
                Sentry.capture("mSearchRootView is Detached");
            }
        } else {
            animator = ViewAnimationUtils.createCircularReveal(mCardView,
                    mCardView.getWidth() - mClearBtn.getWidth() / 2,
                    mClearBtn.getWidth() / 2,
                    mCardView.getWidth(), 0);
            animator.setDuration(250);
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    Utils.toggleKeyboard(false, mSearchEt);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        mCardView.setVisibility(View.GONE);
                        getActivity().getSupportFragmentManager().popBackStack();
                    } catch (Exception e) {
                        e.printStackTrace();
                        FlurryAgent.logEvent("mCardView is null");
                    }
                }
            });
            mSearchRootView.animate().setDuration(300).alpha(0f).start();
        }
        animator.start();
    }

    @Override
    public void onLoadMore(int willLoadPage) {
        mPresenter.search(getQueryStr(), willLoadPage);
    }

    @Override
    public String getQueryStr() {
        return mSearchEt.getText().toString().trim();
    }

    @Override
    public void fillView(BingSearchResultInfo resultInfo, boolean isLoadMore) {
        if (resultInfo == null) {
            mResultAdapter.setData(null);
            mResultRecyV.setVisibility(View.GONE);
            return;
        }
        mResultRecyV.setVisibility(View.VISIBLE);
        mResultAdapter.setData(resultInfo.getItems(), isLoadMore);
        mResultRecyV.setHasMore(resultInfo.hasMore());
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        String link = mResultAdapter.getDatas().get(position).getLink();
        UrlInterceptor.openWapPage(link, getContext());
    }


}
