package me.ghui.v2er.module.topic;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.network.bean.TopicInfo;

/**
 * Created by ghui on 09/05/2017.
 * 主题内容的Item
 */

public class TopicContentItemDelegate extends ItemViewDelegate<TopicInfo.Item> {

    private HtmlView  mHtmlView;

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
        FrameLayout webviewContainer = holder.getView(R.id.htmlview_container);
        View placeHolder = webviewContainer.findViewById(R.id.webview_placeholder);
        if(mHtmlView == null) {
            mHtmlView = new HtmlView(mContext);
            webviewContainer.addView(mHtmlView);
        }
        placeHolder.setVisibility(View.VISIBLE);
        if (Check.notEmpty(contentInfo.getFormattedHtml())) {
            webviewContainer.setVisibility(View.VISIBLE);
            mHtmlView.loadContentView(contentInfo.getFormattedHtml());
            placeHolder.setVisibility(View.GONE);
        } else {
            webviewContainer.setVisibility(View.GONE);
        }
    }

}
