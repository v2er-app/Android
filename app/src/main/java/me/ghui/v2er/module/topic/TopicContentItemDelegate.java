package me.ghui.v2er.module.topic;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.ScaleUtils;

/**
 * Created by ghui on 09/05/2017.
 * 主题内容的Item
 */

public class TopicContentItemDelegate extends ItemViewDelegate<TopicInfo.Item> {

    private FrameLayout mWebviewContainer;
    private HtmlView mHtmlView;
    private String mLastContent;

    public TopicContentItemDelegate(Context context) {
        super(context);
        L.e("TopicContentItemDelegate init------");
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
        L.e("------------convert------");
        HtmlView.OnHtmlRenderListener renderListener = (HtmlView.OnHtmlRenderListener) mContext;
        TopicInfo.ContentInfo contentInfo = (TopicInfo.ContentInfo) item;
        if (mWebviewContainer == null) {
            mWebviewContainer = holder.getView(R.id.htmlview_container);
            mWebviewContainer.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mWebviewContainer.setMinimumHeight(ScaleUtils.getScreenContentH());
            mHtmlView = mWebviewContainer.findViewById(R.id.topic_htmlview);
            mHtmlView.setOnHtmlRenderListener(renderListener);
        }
        String content = contentInfo.getFormattedHtml();
        if (Check.notEmpty(content)) {
            mWebviewContainer.setVisibility(View.VISIBLE);
            boolean isContentChanged = !content.equals(mLastContent);
            L.e("----load content----");
            if (isContentChanged) {
                L.e("----content changed----");
                mHtmlView.loadContentView(content);
                mLastContent = content;
            } else {
                renderListener.onRenderCompleted();
            }
        } else {
            renderListener.onRenderCompleted();
            mWebviewContainer.setVisibility(View.GONE);
        }
    }

}
