package me.ghui.v2er.module.home;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import me.ghui.v2er.util.Theme;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.MultiItemTypeAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.BaseRecyclerView;

/**
 * 首页第一个标签的下拉菜单
 */
public class HomeFilterMenu implements MultiItemTypeAdapter.OnItemClickListener {
    private Context mContext;
    private ViewGroup mContainer;
    private ViewGroup mTabsWrapper;
    private BaseRecyclerView mTabsRecyclerView;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private TextView mTabView;
    private AnimatedVectorDrawable mUpDrawable;
    private AnimatedVectorDrawable mDownDrawable;

    public HomeFilterMenu(ViewGroup container, TextView tabView) {
        mContext = container.getContext();
        mContainer = container;
        mTabView = tabView;
    }

    public void setOnItemClickListner(OnMenuItemClickListener onItemClickListner) {
        mOnMenuItemClickListener = onItemClickListner;
    }

    boolean isShowing() {
        return mTabsWrapper != null && mTabsWrapper.getVisibility() == View.VISIBLE;
    }

    public void toggle() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    public void show() {
        if (mTabsWrapper == null) {
            initView();
        }
        if (mDownDrawable == null) {
            mDownDrawable = (AnimatedVectorDrawable) mTabView.getCompoundDrawables()[2];
        }
        mTabView.setCompoundDrawablesWithIntrinsicBounds(null, null, mDownDrawable, null);
        mDownDrawable.start();
        mTabsWrapper.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fadein));
        mTabsWrapper.setVisibility(View.VISIBLE);
        mTabsRecyclerView.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.news_tabs_menu_slide_down));
        mTabsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void initView() {
        mTabsWrapper = (ViewGroup) LayoutInflater.from(mContext)
                .inflate(R.layout.home_filter_menu, mContainer, false);
        mContainer.addView(mTabsWrapper);
        mTabsWrapper.setOnClickListener(v -> hide());
        mTabsRecyclerView = mTabsWrapper.findViewById(R.id.tabs_recyclerview);
        mTabsRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        CommonAdapter<TabInfo> adapter = new CommonAdapter<TabInfo>(mContext, R.layout.tab_info_item) {
            @Override
            protected void convert(ViewHolder holder, TabInfo tabInfo, int position) {
                holder.setText(R.id.tab_title_tv, tabInfo.title);
            }
        };
        adapter.setData(TabInfo.getDefault());
        mTabsRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    public void hide() {
        if (mUpDrawable == null) {
            mUpDrawable = (AnimatedVectorDrawable) mContext.getResources().getDrawable(R.drawable.animate_triangle_up);
            mUpDrawable.setTint(Theme.getColor(R.attr.tablayout_selected_color, mContext));
        }
        mTabView.setCompoundDrawablesWithIntrinsicBounds(null, null, mUpDrawable, null);
        mUpDrawable.start();
        mTabsRecyclerView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.news_tabs_menu_slide_up));
        Animation fadeout = AnimationUtils.loadAnimation(mContext, R.anim.fadeout);
        fadeout.setStartOffset(50);
        mTabsWrapper.startAnimation(fadeout);
        mTabsRecyclerView.setVisibility(View.GONE);
        mTabsWrapper.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View view, ViewHolder holder, int position) {
        TabInfo tabInfo = TabInfo.getDefault().get(position);
        if (tabInfo.needLogin && !UserUtils.isLogin()) {
            Voast.show("登录后再能查看＂" + tabInfo.title + "＂下的内容");
            return;
        }
        TabInfo.saveSelectTab(tabInfo);
        mTabView.setText(tabInfo.title);
        hide();
        if (mOnMenuItemClickListener != null) {
            mOnMenuItemClickListener.onMenuItemClicked(tabInfo);
        }
    }

    public interface OnMenuItemClickListener {
        void onMenuItemClicked(TabInfo tabInfo);
    }

}
