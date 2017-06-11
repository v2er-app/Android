package me.ghui.v2er.module.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.Stack;

import javax.inject.Inject;

import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import io.reactivex.ObservableTransformer;
import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.injector.component.AppComponent;
import me.ghui.v2er.util.RxUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.PtrMaterialFrameLayout;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseActivity<T extends BaseContract.IPresenter> extends RxActivity implements BaseContract.IView, IBindToLife, IBackHandler {

    protected FrameLayout mRootView;
    protected ViewGroup mContentView;
    @Nullable
    protected Toolbar mToolbar;
    protected View mLoadingView;


    @Inject
    protected T mPresenter;

    private Stack<IBackable> mBackables;

    /**
     * bind a layout resID to the content of this page
     *
     * @return layout res id
     */
    @LayoutRes
    protected abstract int attachLayoutRes();

    /**
     * Set a default Toolbar, if you don't want certain page to have a toolbar,
     * just return null;
     *
     * @return
     */
    protected Toolbar attachToolbar() {
        if (attachToolBar() == 0) {//default
            BaseToolBar baseToolBar = new BaseToolBar(this);
            return baseToolBar;
        } else {
            return (BaseToolBar) getLayoutInflater().inflate(attachToolBar(), null);
        }
    }

    @LayoutRes
    protected int attachToolBar() {
        return 0;
    }

    /**
     * config toolbar here
     */
    protected void configToolBar(Toolbar toolBar) {
        toolBar.setTitle(getTitle());
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
    }

    /**
     * if you want to support ptr, then attach a PtrHandler to it
     *
     * @return PtrHandler
     */
    protected PtrHandler attachPtrHandler() {
        return null;
    }


    @Override
    public void handleBackable(IBackable backable) {
        if (mBackables == null) {
            mBackables = new Stack<>();
        }
        if (!mBackables.contains(backable)) {
            mBackables.push(backable);
        }
    }

    @Override
    public void onBackPressed() {
        if (Utils.isnodempty(mBackables)) {
            mBackables.pop().onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackables != null) {
            mBackables.clear();
        }
    }

    /**
     * push a fragment to the fragment stack
     *
     * @param fragment
     */
    public void pushFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(mRootView.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * just add a fragment to the top of the activity, unbackable
     *
     * @param fragment
     */
    public void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(mRootView.getId(), fragment)
                .commit();
    }

    /**
     * init Dagger2 injector
     */
    protected void startInject() {

    }

    /**
     * parse the intent extra data
     */
    protected void parseExtras(Intent intent) {

    }

    /**
     * init views in this page
     */
    protected void init() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(onCreateRootView());
        ButterKnife.bind(this);
        startInject();
        parseExtras(getIntent());
        init();
        autoLoad();
    }

    protected void autoLoad() {
        if (getPtrLayout() != null) getPtrLayout().autoRefresh();
    }

    protected ViewGroup onCreateRootView() {
        if ((mToolbar = attachToolbar()) != null) {
            LinearLayout rootView = new LinearLayout(this);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.setOrientation(LinearLayout.VERTICAL);
            rootView.addView(mToolbar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            mContentView = rootView;

            configToolBar(mToolbar);
        }

        ViewGroup viewBelowToolbar;
        if (attachPtrHandler() != null) {
            PtrMaterialFrameLayout ptrLayout = new PtrMaterialFrameLayout(this);
            View content = getLayoutInflater().inflate(attachLayoutRes(), ptrLayout, false);
            ptrLayout.setContentView(content);
            ptrLayout.setPtrHandler(attachPtrHandler());
            ptrLayout.setPinContent(true);
            viewBelowToolbar = ptrLayout;
        } else {
            viewBelowToolbar = (ViewGroup) getLayoutInflater().inflate(attachLayoutRes(), null);
        }

        if (mContentView != null) { //has toolbar
            mContentView.addView(viewBelowToolbar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            mContentView = viewBelowToolbar;
            mContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }

        mRootView = new FrameLayout(this);
        mRootView.setId(R.id.root_view_framelayout);
        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRootView.addView(mContentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return mRootView;
    }

    @Nullable
    protected PtrFrameLayout getPtrLayout() {
        if (attachPtrHandler() == null) return null;
        PtrFrameLayout ptrLayout;
        if (mToolbar != null) {
            ptrLayout = (PtrFrameLayout) mContentView.getChildAt(1);
        } else {
            ptrLayout = (PtrFrameLayout) mContentView;
        }
        return ptrLayout;
    }

    protected AppCompatActivity getActivity() {
        return this;
    }

    protected Context getContext() {
        return this;
    }

    protected AppComponent getAppComponent() {
        return App.get().getAppComponent();
    }

    protected View onCreateLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = LayoutInflater.from(this).inflate(R.layout.base_loading_view, mRootView, false);
            mRootView.addView(mLoadingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            mLoadingView.bringToFront();
        }
        return mLoadingView;
    }

    @Override
    public void showLoading() {
        onCreateLoadingView();
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (getPtrLayout() != null) {
            getPtrLayout().refreshComplete();
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public <K> LifecycleTransformer<K> bindToLife() {
        return bindToLifecycle();
    }

    @Override
    public <K> ObservableTransformer<K, K> rx() {
        return rx(this);
    }

    @Override
    public <K> ObservableTransformer<K, K> rx(IViewLoading viewLoading) {
        return RxUtils.rx(this, viewLoading);
    }

    protected void delay(long millisecond, Runnable runnable) {
        mContentView.postDelayed(runnable, millisecond);
    }

    protected void post(Runnable runnable) {
        delay(0, runnable);
    }

    protected static String KEY(String key) {
        return Utils.KEY(key);
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V $(int id) {
        return (V) findViewById(id);
    }
}
