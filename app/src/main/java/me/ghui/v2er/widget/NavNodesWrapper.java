package me.ghui.v2er.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;

import com.google.android.flexbox.FlexboxLayout;

import java.util.List;
import java.util.Stack;

import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.module.node.NodeTopicActivity;
import me.ghui.v2er.network.bean.NodesNavInfo;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.FontSizeUtil;
import android.util.TypedValue;

/**
 * Created by ghui on 22/05/2017.
 */

public class NavNodesWrapper extends FlexboxLayout implements View.OnClickListener {

    private Stack<TagView> sPool = new Stack<>();

    public NavNodesWrapper(Context context) {
        super(context);
    }

    public NavNodesWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavNodesWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(List<NodesNavInfo.Item.NodeItem> nodes) {
        for (int i = 0; i < Utils.listSize(nodes); i++) {
            NodesNavInfo.Item.NodeItem item = nodes.get(i);
            TagView view = obtainChild(i);
            view.setText(item.getName());
            view.setTag(item.getLink());
            view.setOnClickListener(this);
            // Apply font size scaling for node tags
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getSubTextSize());
        }
        int childCount = getChildCount();
        int start = Utils.listSize(nodes);
        for (int i = start; i < childCount; i++) {
            sPool.push((TagView) getChildAt(i));
        }
        removeViews(start, childCount - start);
    }

    private TagView obtainChild(int index) {
        TagView tagView = (TagView) getChildAt(index);
        if (tagView == null) {
            if (Check.isEmpty(sPool)) {
                tagView = new TagView(new ContextThemeWrapper(getContext(), R.style.TagView));
            } else {
                tagView = sPool.pop();
            }
            addView(tagView);
        }
        return tagView;
    }

    @Override
    public void onClick(View v) {
        String link = UriUtils.getLastSegment((String) v.getTag());
        if (Check.isEmpty(link)) return;
        NodeTopicActivity.open(link, getContext());
    }
}
