package me.ghui.v2er.module.vshare;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.util.DarkModelUtils;
import me.ghui.v2er.widget.BaseToolBar;

/**
 * Fullscreen WebView Activity for displaying vshare page
 * with automatic theme adaptation
 */
public class VshareWebActivity extends BaseActivity<BaseContract.IPresenter> {

    private static final String TAG = "VshareWebActivity";
    private static final String VSHARE_BASE_URL = "https://v2er.app/vshare";

    @BindView(R.id.webview)
    WebView mWebView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    public static void open(Context context) {
        Intent intent = new Intent(context, VshareWebActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_vshare_web;
    }

    @Nullable
    @Override
    protected BaseToolBar attachToolbar() {
        // Return null to hide the toolbar for fullscreen WebView experience
        return null;
    }

    @Override
    protected void reloadMode(int mode) {
        // Recreate activity to apply new theme
        recreate();
    }

    @Override
    protected boolean supportSlideBack() {
        // Disable slide back for fullscreen WebView
        return false;
    }

    @Override
    protected void init() {
        super.init();

        // Apply fullscreen flags for edge-to-edge WebView
        View decorView = getWindow().getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        // Set status bar icon color based on theme
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!DarkModelUtils.isDarkMode()) {
                // Light mode: use dark status bar icons
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }

        decorView.setSystemUiVisibility(systemUiVisibility);

        setupWebView();

        // Compute URL with theme parameter based on current app theme
        String url = VSHARE_BASE_URL;
        if (DarkModelUtils.isDarkMode()) {
            url += "?theme=dark";
        } else {
            url += "?theme=light";
        }
        mWebView.loadUrl(url);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = mWebView.getSettings();

        // Enable JavaScript
        settings.setJavaScriptEnabled(true);

        // Enable DOM storage
        settings.setDomStorageEnabled(true);

        // Disable file and content access for security
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);

        // Enable Safe Browsing if API >= 26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }

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
                return handleUrlLoading(request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrlLoading(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

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

    /**
     * Handle URL loading for WebView
     * Returns true if the URL was handled externally, false if WebView should load it
     */
    private boolean handleUrlLoading(String url) {
        if (url == null) {
            return false;
        }

        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();

        // Handle intent:// URLs (e.g., Google Play Store links)
        if ("intent".equals(scheme)) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                // Check if there's an app that can handle this intent
                if (getPackageManager().resolveActivity(intent, 0) != null) {
                    startActivity(intent);
                    return true;
                }

                // Fallback: Try to open the browser_fallback_url if available
                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                if (fallbackUrl != null) {
                    mWebView.loadUrl(fallbackUrl);
                    return true;
                }

                // Last resort: Try to open in Google Play if it's a Play Store intent
                String packageName = intent.getPackage();
                if (packageName != null) {
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + packageName));
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (getPackageManager().resolveActivity(marketIntent, 0) != null) {
                        startActivity(marketIntent);
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error handling intent URL: " + url, e);
                Toast.makeText(this, "Unable to open app", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        // Handle market:// URLs (Google Play Store)
        if ("market".equals(scheme)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "Google Play Store not found", e);
                // Fallback to web version
                String webUrl = url.replace("market://", "https://play.google.com/store/apps/");
                mWebView.loadUrl(webUrl);
                return true;
            }
        }

        // Handle other app-specific schemes (e.g., mailto:, tel:, etc.)
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "No app found to handle scheme: " + scheme, e);
                Toast.makeText(this, "No app found to open this link", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        // Let WebView handle normal http/https URLs
        return false;
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
            mWebView.clearCache(false);
            mWebView.loadUrl("about:blank");
            mWebView.pauseTimers();
            mWebView.destroy();
        }
        super.onDestroy();
    }
}
