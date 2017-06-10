package me.ghui.v2er.module.base;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import io.reactivex.ObservableTransformer;
import me.ghui.v2er.general.App;
import me.ghui.v2er.injector.component.AppComponent;
import me.ghui.v2er.util.RxUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.PtrMaterialFrameLayout;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseFragment<T extends BaseContract.IPresenter> extends RxFragment implements BaseContract.IView, IBindToLife {

    protected ViewGroup mRootView;

    @Inject
    protected T mPresenter;
    private boolean mHasAutoLoaded = false;
    private boolean isPageShow;

    /**
     * if you want to support ptr, then attach a PtrHandler to it
     *
     * @return PtrHandler
     */
    protected PtrHandler attachPtrHandler() {
        return null;
    }

    /**
     * bind a layout resID to the content of this page
     *
     * @return
     */
    @LayoutRes
    protected abstract int attachLayoutRes();

    /**
     * init Dagger2 injector
     */
    protected abstract void startInject();

    /**
     * init views in this page
     */
    protected abstract void init();


    @Nullable
    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreateRootView(inflater, container);
        ButterKnife.bind(this, mRootView);
        startInject();
        init();
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isPageShow) {
            Logger.d("lazyLoad in onViewCreated");
            lazyLoad();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (this instanceof IBackable && getActivity() instanceof IBackHandler) {
            IBackHandler backHandler = (IBackHandler) getActivity();
            backHandler.handleBackable((IBackable) this);
        }
    }

    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container) {
        Logger.d("onCreateRootView");
        if (attachPtrHandler() != null) {
            if (mRootView == null) {
                PtrMaterialFrameLayout ptrLayout = new PtrMaterialFrameLayout(getContext());
                ptrLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT));
                View content = inflater.inflate(attachLayoutRes(), ptrLayout, false);
                ptrLayout.setContentView(content);
                ptrLayout.setPtrHandler(attachPtrHandler());
                ptrLayout.setPinContent(true);
                mRootView = ptrLayout;
            }
        } else {
            if (mRootView == null) {
                mRootView = (ViewGroup) inflater.inflate(attachLayoutRes(), container, false);
            }
        }
        return mRootView;
    }

    @Nullable
    protected PtrFrameLayout getPtrLayout() {
        if (attachPtrHandler() != null) return (PtrFrameLayout) mRootView;
        else return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.i("onActivityCreated." + this.getClass().getSimpleName());
    }


    /**
     * fetch data from server to views in this page
     * , default auto load
     */
    protected void lazyLoad() {
        if (getPtrLayout() != null && !mHasAutoLoaded) {
            getPtrLayout().autoRefresh();
            mHasAutoLoaded = true;
        }
    }

    @CallSuper
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isPageShow = true;
            onFragmentShow();
        } else {
            isPageShow = false;
            onFragmentHide();
        }
    }

    protected void onFragmentShow() {
        if (mRootView != null) {
            Logger.d("lazyLoad in onFragmentShow");
            lazyLoad();
        }
    }

    protected void onFragmentHide() {

    }

    protected AppComponent getAppComponent() {
        return App.get().getAppComponent();
    }

    protected void delay(long millisecond, Runnable runnable) {
        mRootView.postDelayed(runnable, millisecond);
    }

    protected void post(Runnable runnable) {
        delay(0, runnable);
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
        if (getPtrLayout() != null) getPtrLayout().refreshComplete();
    }

    @Override
    public <K> LifecycleTransformer<K> bindToLife() {
        return bindToLifecycle();
    }

    @Override
    public <K> ObservableTransformer<K, K> rx() {
        return RxUtils.rx(this, this);
    }

    @Override
    public <K> ObservableTransformer<K, K> rx(IViewLoading viewLoading) {
        return RxUtils.rx(this, viewLoading);
    }

    protected void toast(String msg) {
        Utils.toast(msg);
    }

    protected void toast(String msg, boolean isToastLong) {
        Utils.toast(msg, isToastLong);
    }
}
