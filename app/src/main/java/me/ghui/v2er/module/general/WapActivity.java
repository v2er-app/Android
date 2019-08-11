package me.ghui.v2er.module.general;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.ColorModeReloader;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.general.ShareManager;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.network.UrlInterceptor;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;

/**
 * Created by ghui on 30/06/2017.
 */

public class WapActivity extends BaseActivity {

    public static String URL_KEY = KEY("wap_url_value");
    public static String FORCH_OPENED_WEBVIEW = KEY("forch_opened_webview");

    @BindView(R.id.webview)
    public WebView mWebView;
    protected String mStartUrl;
    protected String mCurrentUrl;
    private boolean mForchOpenedInWebView;

    public static void open(String url, Context context, boolean forchOpenedInWebView) {
        Navigator.from(context)
                .putExtra(URL_KEY, url)
                .putExtra(FORCH_OPENED_WEBVIEW, forchOpenedInWebView)
                .to(WapActivity.class)
                .start();
    }

    @Override
    protected void parseExtras(Intent intent) {
        mStartUrl = intent.getStringExtra(URL_KEY);
        mForchOpenedInWebView = intent.getBooleanExtra(FORCH_OPENED_WEBVIEW, false);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.wap_layout;
    }

    @Override
    protected void init() {
        mWebView.setWebChromeClient(new BaseWebChromeClient());
        mWebView.setWebViewClient(new BaseWebViewClient());
        configWebView(mWebView.getSettings());
        firstLoad(null);
    }

    @Override
    protected void reloadMode(int mode) {
        ColorModeReloader.target(this)
                .putExtra(URL_KEY, mStartUrl)
                .putExtra(FORCH_OPENED_WEBVIEW, mForchOpenedInWebView)
                .reload();
    }

    protected void firstLoad(Map<String, String> headers) {
        loadUrl(mStartUrl, headers);
    }

    protected void configWebView(WebSettings settings) {
        settings.setJavaScriptEnabled(true);
    }

    @Override
    protected void configToolBar(BaseToolBar toolBar) {
        super.configToolBar(toolBar);
        Utils.setPaddingForStatusBar(toolBar);
        mToolbar.inflateMenu(R.menu.wapview_menu);
        mToolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_refresh:
                    showLoading();
                    refresh();
                    break;
                case R.id.action_share:
                    ShareManager.ShareData shareData = new ShareManager.ShareData.Builder(toolBar.getTitle().toString())
                            .link(mCurrentUrl)
                            .content("链接分享")
                            .build();
                    ShareManager shareManager = new ShareManager(shareData, this);
                    shareManager.showShareDialog();
                    break;
                case R.id.action_copy_url:
                    Utils.copyToClipboard(this, mCurrentUrl);
                    toast("链接已拷贝成功");
                    break;
                case R.id.action_open_in_browser:
                    Utils.openInBrowser(mCurrentUrl, this);
                    break;
            }
            return false;
        });
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener attachOnRefreshListener() {
        return () -> refresh();
    }

    protected void refresh() {
        loadUrl(mCurrentUrl);
    }

    protected void loadUrl(String url) {
        loadUrl(url, null);
    }

    protected void loadUrl(String url, Map<String, String> headers) {
        if (url != null) {
            mCurrentUrl = url;
            L.d("set current url: " + mCurrentUrl);
            if (headers != null) {
                mWebView.loadUrl(url, headers);
            } else {
                mWebView.loadUrl(url);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.resumeTimers();
        mWebView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewGroup viewParent = (ViewGroup) mWebView.getParent();
        if (viewParent != null) {
            viewParent.removeView(mWebView);
        }
        mWebView.removeAllViews();
        mWebView.destroy();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack() && mWebView.isShown()) {
            goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void goBack() {
        mWebView.goBack();
        WebBackForwardList backForwardList = mWebView.copyBackForwardList();
        setTitle(backForwardList.getCurrentItem().getTitle());
    }

    protected void onWapPageStarted(WebView webview, String url) {
        showLoading();
    }

    protected void onWapProgressChanged(int progress) {

    }

    protected void onWapPageFinished(WebView webview, String url) {
        hideLoading();
    }

    protected void onWapReceivedError(int errorCode, String description) {

    }

    protected void onWapReceivedSslError(SslErrorHandler sslErrorHandler, SslError error) {

    }

    protected boolean checkIntercept(String currentUrl) {
        return UrlInterceptor.intercept(currentUrl, this, true, mForchOpenedInWebView);
    }

    protected void onWapReceivedTitle(String title) {
        if (title != null && title.startsWith("V2EX › ")) {
            title = title.replace("V2EX › ", "");
        }
        setTitle(title);
    }

    protected boolean onWapJsAlert(String url, String message, JsResult jsResult) {

        return false;
    }


    private class BaseWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mCurrentUrl = url;
            onWapPageStarted(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url) || checkIntercept(url);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            onWapPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            onWapReceivedError(errorCode, description);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            onWapReceivedSslError(handler, error);
        }

    }

    private class BaseWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            onWapReceivedTitle(title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return onWapJsAlert(url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            onWapProgressChanged(newProgress);
        }
    }
}
