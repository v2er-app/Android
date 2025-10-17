package me.ghui.v2er.module.vshare;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.base.BaseContract;
import me.ghui.v2er.util.DarkModelUtils;
import me.ghui.v2er.util.Utils;
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

    @BindView(R.id.theme_overlay)
    View mThemeOverlay;

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

        // Set loading delay to 0 to show loading indicator immediately
        setFirstLoadingDelay(0);

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

        // Set WebView top margin to status bar height
        applyStatusBarMargin();

        // Set overlay background color to match theme
        setupThemeOverlay();

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

    /**
     * Setup theme overlay to prevent white flash during page load
     */
    private void setupThemeOverlay() {
        boolean isDarkMode = DarkModelUtils.isDarkMode();
        if (isDarkMode) {
            // Dark mode: use dark overlay
            mThemeOverlay.setBackgroundColor(Color.parseColor("#1a1a1a"));
        } else {
            // Light mode: use white overlay
            mThemeOverlay.setBackgroundColor(Color.WHITE);
        }
        // Overlay is visible by default, will be hidden after theme is applied
        mThemeOverlay.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        // Set WebView background color to match theme
        boolean isDarkMode = DarkModelUtils.isDarkMode();
        if (isDarkMode) {
            // Dark mode: set dark background
            mWebView.setBackgroundColor(Color.parseColor("#1a1a1a"));
        } else {
            // Light mode: set white background
            mWebView.setBackgroundColor(Color.WHITE);
        }

        WebSettings settings = mWebView.getSettings();

        // Enable JavaScript
        settings.setJavaScriptEnabled(true);

        // Enable DOM storage
        settings.setDomStorageEnabled(true);

        // Force dark mode for WebView content on Android Q+ if app is in dark mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isDarkMode) {
            settings.setForceDark(WebSettings.FORCE_DARK_ON);
        }

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
                showLoading();
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

                // Hide overlay after theme is applied (small delay to ensure JS executes)
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Fade out the overlay
                        mThemeOverlay.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mThemeOverlay.setVisibility(View.GONE);
                                    }
                                });
                        hideLoading();
                    }
                }, 100);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                // Hide loading indicator on error to prevent persistent spinner
                hideLoading();
                mThemeOverlay.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedHttpError(WebView view, android.webkit.WebResourceRequest request,
                                           android.webkit.WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                // Hide loading indicator on HTTP error to prevent persistent spinner
                hideLoading();
                mThemeOverlay.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Apply status bar height as top margin and navigation bar height as bottom margin to WebView
     */
    private void applyStatusBarMargin() {
        int statusBarHeight = getStatusBarHeight();
        int navigationBarHeight = Utils.getNavigationBarHeight();

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mWebView.getLayoutParams();
        if (params instanceof FrameLayout.LayoutParams) {
            if (statusBarHeight > 0) {
                params.topMargin = statusBarHeight;
            }
            if (navigationBarHeight > 0) {
                params.bottomMargin = navigationBarHeight;
            }
            mWebView.setLayoutParams(params);
        }
    }

    /**
     * Get status bar height dynamically
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
