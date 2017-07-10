package me.ghui.v2er.module.general;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.network.UrlInterceptor;

/**
 * Created by ghui on 30/06/2017.
 */

public class WapActivity extends BaseActivity {

    public static String URL_KEY = KEY("wap_url_value");

    @BindView(R.id.webview)
    WebView mWebView;
    private String mCurrentUrl;

    public static void open(String url, Context context) {
        Navigator.from(context)
                .putExtra(URL_KEY, url)
                .to(WapActivity.class)
                .start();
    }

    @Override
    protected void parseExtras(Intent intent) {
        mCurrentUrl = intent.getStringExtra(URL_KEY);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.wap_layout;
    }


    @Override
    protected void init() {
        mWebView.setWebChromeClient(new BaseWebChromeClient());
        mWebView.setWebViewClient(new BaseWebViewClient());
        loadUrl(mCurrentUrl);
    }

    protected void loadUrl(String url) {
        if (url != null) {
            mCurrentUrl = url;
            Logger.d("set current url: " + mCurrentUrl);
            mWebView.loadUrl(url);
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
        return UrlInterceptor.intercept(currentUrl, this, false);
    }

    protected void onWapReceivedTitle(String title) {
        setTitle(title, mCurrentUrl);
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
