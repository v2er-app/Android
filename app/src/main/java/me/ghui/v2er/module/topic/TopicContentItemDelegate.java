package me.ghui.v2er.module.topic;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.widget.AppendTopicContentView;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 09/05/2017.
 * 主题内容的Item（不包含附言）
 */

public class TopicContentItemDelegate extends ItemViewDelegate<TopicInfo.Item> {

    public TopicContentItemDelegate(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        // 加载中的占位图
        return R.layout.topic_content_webview_item;
    }

    @Override
    public boolean isForViewType(TopicInfo.Item item, int position) {
        return item != null && item.isContentItem();
    }

    @Override
    public void convert(ViewHolder holder, TopicInfo.Item item, int position) {
        TopicInfo.ContentInfo contentInfo = (TopicInfo.ContentInfo) item;
        WebView contentWebView = holder.getView(R.id.content_webview);
        if (Check.notEmpty(contentInfo.getContentHtml())) {
            contentWebView.setVisibility(View.VISIBLE);
            contentWebView.loadData(contentInfo.getContentHtml(), null, null);
        } else {
            contentWebView.setVisibility(View.GONE);
        }
    }
}
