package me.ghui.v2er.module.topic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.ScaleUtils;

/**
 * Created by ghui on 09/05/2017.
 * 主题内容的Item
 */

public class TopicContentItemDelegate extends ItemViewDelegate<TopicInfo.Item> {

    private HtmlView mHtmlView;

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
        String content = contentInfo.getFormattedHtml();
        FrameLayout webviewContainer = holder.getView(R.id.htmlview_container);
        webviewContainer.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        webviewContainer.setMinimumHeight(ScaleUtils.getScreenContentH());
        HtmlView.OnHtmlRenderListener renderListener = (HtmlView.OnHtmlRenderListener) mContext;
        if (Check.notEmpty(content)) {
            if (mHtmlView == null) {
                mHtmlView = new HtmlView(mContext);
                mHtmlView.setOnHtmlRenderListener(renderListener);
                webviewContainer.addView(mHtmlView);
            }
            webviewContainer.setVisibility(View.VISIBLE);
            mHtmlView.loadContentView(content);
        } else {
            webviewContainer.setVisibility(View.GONE);
        }
    }

}
