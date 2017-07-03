package me.ghui.v2er.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import me.ghui.v2er.R;

/**
 * Created by ghui on 03/07/2017.
 */

public class TopicWebView extends WebView {
    public TopicWebView(Context context) {
        super(context);
        init();
    }

    public TopicWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopicWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TopicWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setTransitionGroup(true);
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDefaultFontSize(15);
    }

}
