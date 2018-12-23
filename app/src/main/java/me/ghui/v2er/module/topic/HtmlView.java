package me.ghui.v2er.module.topic;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import me.ghui.toolbox.android.Assets;
import me.ghui.v2er.module.gallery.GalleryActivity;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.util.Utils;

public class HtmlView extends WebView {

    private OnHtmlRenderListener onHtmlRenderListener;

    public void setOnHtmlRenderListener(OnHtmlRenderListener onHtmlRenderListener) {
        this.onHtmlRenderListener = onHtmlRenderListener;
    }

    public HtmlView(Context context) {
        super(context);
        init();
    }

    public HtmlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        setWebViewClient(new V2exWebViewClient());
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        addJavascriptInterface(new ImgClickJSInterface(), "imagelistener");
    }

    public void loadContentView(String contentStr) {
        // add css style to contentView
        String container = Assets.getString("html/v2er.html", getContext());
        String formattedStr = String.format(container, contentStr);
        loadDataWithBaseURL("file:///android_asset/html/", formattedStr, "text/html", "UTF-8", null);
    }

    private class V2exWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Utils.openWap(request.getUrl().toString(), getContext());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(onHtmlRenderListener != null) {
                onHtmlRenderListener.onRenderCompleted();
            }
            addIMGClickListener();
        }

        private void addIMGClickListener() {
            HtmlView.this.post(() -> HtmlView.this.loadUrl("javascript:addClickToImg()"));
        }

    }

    private class ImgClickJSInterface {
        @JavascriptInterface
        public void openImage(int index, String[] imgs) {
            ImagesInfo imagesInfo = new ImagesInfo(index, imgs);
            GalleryActivity.open(imagesInfo, getContext());
        }

    }

    public interface OnHtmlRenderListener {
        void onRenderCompleted();
    }

}
