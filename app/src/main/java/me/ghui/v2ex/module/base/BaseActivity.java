package me.ghui.v2ex.module.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import io.reactivex.ObservableTransformer;
import me.ghui.v2ex.R;
import me.ghui.v2ex.general.App;
import me.ghui.v2ex.injector.component.AppComponent;
import me.ghui.v2ex.util.RxUtils;
import me.ghui.v2ex.util.ScaleUtils;
import me.ghui.v2ex.util.Utils;
import me.ghui.v2ex.widget.BaseToolBar;
import me.ghui.v2ex.widget.PtrMaterialFrameLayout;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseActivity<T extends BaseContract.IPresenter> extends RxActivity implements BaseContract.IView, IBindToLife {

    protected ViewGroup mRootView;
    @Nullable
    protected BaseToolBar mToolbar;

    @Inject
    protected T mPresenter;

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
    protected BaseToolBar attachToolbar() {
        if (attachToolBar() == 0) {//default
            BaseToolBar baseToolBar = new BaseToolBar(this);
            baseToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black);
            baseToolBar.setElevation(ScaleUtils.dp(2, getContext()));
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
    protected void configToolBar(BaseToolBar toolBar) {
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
            mRootView = rootView;

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

        if (mRootView != null) { //has toolbar
            mRootView.addView(viewBelowToolbar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            mRootView = viewBelowToolbar;
            mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return mRootView;
    }

    @Nullable
    protected PtrFrameLayout getPtrLayout() {
        if (attachPtrHandler() == null) return null;
        PtrFrameLayout ptrLayout;
        if (mToolbar != null) {
            ptrLayout = (PtrFrameLayout) mRootView.getChildAt(1);
        } else {
            ptrLayout = (PtrFrameLayout) mRootView;
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

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {
        if (getPtrLayout() != null) {
            getPtrLayout().refreshComplete();
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
        mRootView.postDelayed(runnable, millisecond);
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
