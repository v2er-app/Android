package me.ghui.v2er.module.base;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import io.reactivex.ObservableTransformer;
import me.ghui.v2er.R;
import me.ghui.v2er.general.App;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.injector.component.AppComponent;
import me.ghui.v2er.module.home.MainActivity;
import me.ghui.v2er.module.login.LoginActivity;
import me.ghui.v2er.module.login.TwoStepLoginActivity;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.network.GeneralError;
import me.ghui.v2er.network.ResultCode;
import me.ghui.v2er.network.bean.TwoStepLoginInfo;
import me.ghui.v2er.util.RxUtils;
import me.ghui.v2er.util.UserUtils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.V2erPtrFrameLayout;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ghui on 05/03/2017.
 */

public abstract class BaseFragment<T extends BaseContract.IPresenter> extends RxFragment implements BaseContract.IView, IBindToLife {

    protected FrameLayout mRootView;
    private View mLoadingView;

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
        if (supportShareElement()) {
            postponeEnterTransition();
        }
        if (showLoadingOnCreateView()) {
            showLoading();
        }
        init();
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    protected boolean showLoadingOnCreateView() {
        return true;
    }


    protected boolean supportShareElement() {
        return false;
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
            IBackHandler handler = (IBackHandler) getActivity();
            handler.handleBackable((IBackable) this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this instanceof IBackable && getActivity() instanceof IBackHandler) {
            IBackHandler handler = (IBackHandler) getActivity();
            handler.popBackable((IBackable) this);
        }
    }

    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container) {
        Logger.d("onCreateRootView");
        if (mRootView == null) {
            View contentView;
            if (attachPtrHandler() != null) {
                V2erPtrFrameLayout ptrLayout = new V2erPtrFrameLayout(getContext());
                ptrLayout.setId(R.id.frag_ptr_layout);
                ptrLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT));
                View content = inflater.inflate(attachLayoutRes(), ptrLayout, false);
                ptrLayout.setContentView(content);
                ptrLayout.setPtrHandler(attachPtrHandler());
                contentView = ptrLayout;
            } else {
                contentView = inflater.inflate(attachLayoutRes(), container, false);
            }
            mRootView = new FrameLayout(getActivity());
            mRootView.setId(R.id.frag_root_view_framelayout);
            mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mRootView.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return mRootView;
    }

    @Nullable
    protected PtrFrameLayout getPtrLayout() {
        if (attachPtrHandler() != null) {
            return (PtrFrameLayout) mRootView.findViewById(R.id.frag_ptr_layout);
        } else return null;
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
//            getPtrLayout().autoRefresh();
            mPresenter.start();
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

    @Override
    public void delay(long millisecond, Runnable runnable) {
        mRootView.postDelayed(runnable, millisecond);
    }

    protected void post(Runnable runnable) {
        delay(0, runnable);
    }


    protected View onCreateLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = LayoutInflater.from(getActivity()).inflate(R.layout.base_loading_view, mRootView, false);
            mRootView.addView(mLoadingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            mLoadingView.bringToFront();
        }
        return mLoadingView;
    }

    @Override
    public void showLoading() {
//        if (attachPtrHandler() != null) return;
        if (mLoadingView != null && mLoadingView.getVisibility() == View.VISIBLE) return;
        if (getPtrLayout() != null && getPtrLayout().isRefreshing()) return;
        onCreateLoadingView();
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (getPtrLayout() != null) getPtrLayout().refreshComplete();
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
        return RxUtils.rxFragment(this, this);
    }

    @Override
    public <K> ObservableTransformer<K, K> rx(int page) {
        return this.rx(page == 1 ? this : null);
    }

    @Override
    public <K> ObservableTransformer<K, K> rx(IViewLoading viewLoading) {
        return RxUtils.rxFragment(this, viewLoading);
    }

    @Override
    public void handleError(GeneralError generalError) {
        if (supportShareElement()) {
            startPostponedEnterTransition();
        }
        if (generalError.getErrorCode() == ResultCode.LOGIN_EXPIRED || generalError.getErrorCode() == ResultCode.LOGIN_NEEDED) {
            handleNotLoginError(generalError.getErrorCode(), generalError.getMessage());
        } else if (generalError.getErrorCode() == ResultCode.REDIRECT_TO_HOME) {
            Navigator.from(getActivity()).setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP).to(MainActivity.class).start();
            getActivity().finish();
        } else if (generalError.getErrorCode() == ResultCode.LOGIN_TWO_STEP) {
            String once = APIService.fruit().fromHtml(generalError.getResponse(), TwoStepLoginInfo.class).getOnce();
            TwoStepLoginActivity.open(once, getActivity());
        } else {
            toast(generalError.getMessage());
        }
    }

    protected void handleNotLoginError(int errCode, String errorMsg) {
        toast(errorMsg);
        UserUtils.clearLogin();
        Navigator.from(getContext())
                .to(LoginActivity.class).start();
    }

    @Override
    public void toast(String msg) {
        Voast.show(msg);
    }

    public void toast(String msg, boolean isToastLong) {
        Voast.show(msg, isToastLong);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}
