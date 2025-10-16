package me.ghui.v2er.module.vshare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ghui.v2er.R;
import me.ghui.v2er.util.DarkModelUtils;

/**
 * Fullscreen WebView Activity for displaying vshare page
 * with automatic theme adaptation
 */
public class VshareWebActivity extends AppCompatActivity {

    private static final String KEY_URL = "url";
    private static final String VSHARE_BASE_URL = "https://v2er.app/vshare";

    @BindView(R.id.webview)
    WebView mWebView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    public static void open(Context context) {
        Intent intent = new Intent(context, VshareWebActivity.class);
        // Append theme parameter based on current app theme
        String url = VSHARE_BASE_URL;
        if (DarkModelUtils.isDarkMode()) {
            url += "?theme=dark";
        } else {
            url += "?theme=light";
        }
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_vshare_web);
        ButterKnife.bind(this);

        setupWebView();

        // Load the URL
        String url = getIntent().getStringExtra(KEY_URL);
        if (url != null) {
            mWebView.loadUrl(url);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = mWebView.getSettings();

        // Enable JavaScript
        settings.setJavaScriptEnabled(true);

        // Enable DOM storage
        settings.setDomStorageEnabled(true);

        // Enable responsive layout
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable zoom
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // Set cache mode
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Set WebViewClient to handle navigation
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Keep navigation within the WebView
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);

                // Inject CSS to ensure proper theme is applied
                String theme = DarkModelUtils.isDarkMode() ? "dark" : "light";
                String js = "javascript:(function() { " +
                        "document.documentElement.setAttribute('data-theme', '" + theme + "'); " +
                        "})()";
                mWebView.loadUrl(js);
            }
        });

        // Set WebChromeClient for progress updates
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Handle back navigation in WebView
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.pauseTimers();
            mWebView.destroy();
        }
        super.onDestroy();
    }
}