package me.ghui.v2ex.module.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2ex.R;
import me.ghui.v2ex.module.base.BaseFragment;
import me.ghui.v2ex.util.Utils;
import me.ghui.v2ex.widget.BaseRecyclerView;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 02/06/2017.
 */

public class SearchFragment extends BaseFragment<SearchContract.IPresenter> implements SearchContract.IView {

    @BindView(R.id.search_result_recycler_view)
    LoadMoreRecyclerView mLoadMoreRecyV;
    @BindView(R.id.image_search_back)
    ImageButton mBackBtn;
    @BindView(R.id.clear_search_img)
    ImageButton mClearBtn;
    @BindView(R.id.search_edit_text)
    EditText mSearchEt;
    @BindView(R.id.search_history_recycler_view)
    BaseRecyclerView mSearchHistoryRecyV;
    @BindView(R.id.search_cardview)
    CardView mCardView;

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

    }

    @Override
    protected void init() {

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
        super.onDestroyView();
    }

    @OnClick(R.id.image_search_back)
    void onBackClicked() {
        animateSearchbar(false);
    }

    @OnClick(R.id.clear_search_img)
    void onClearClicked() {
        if (Utils.isEmpty(mSearchEt.getText())) {
            animateSearchbar(false);
        } else {
            mSearchEt.setText(null);
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
            animator.setDuration(400);
        } else {
            animator = ViewAnimationUtils.createCircularReveal(mCardView,
                    mCardView.getWidth() - mClearBtn.getWidth() / 2,
                    mClearBtn.getWidth() / 2,
                    mCardView.getWidth(), 0);
            animator.setDuration(250);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCardView.setVisibility(View.GONE);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
        animator.start();
    }

}
