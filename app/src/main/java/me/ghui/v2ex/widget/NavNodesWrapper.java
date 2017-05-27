package me.ghui.v2ex.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.flexbox.FlexboxLayout;

import java.util.List;
import java.util.Stack;

import me.ghui.v2ex.general.Navigator;
import me.ghui.v2ex.module.node.NodeTopicActivity;
import me.ghui.v2ex.network.bean.NodesNavInfo;
import me.ghui.v2ex.util.Utils;

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
        }

        //recy
        for (int i = Utils.listSize(nodes); i < getChildCount(); i++) {
            sPool.push((TagView) getChildAt(i));
            removeViewAt(i);
        }
    }

    private TagView obtainChild(int index) {
        TagView tagView = (TagView) getChildAt(index);
        if (tagView == null) {
            if (Utils.isEmpty(sPool)) {
                tagView = new TagView(getContext());
            } else {
                tagView = sPool.pop();
            }
            addView(tagView);
        }
        return tagView;
    }

    @Override
    public void onClick(View v) {
        String link = (String) v.getTag();
        if (Utils.isEmpty(link)) return;
        Navigator.from(getContext())
                .to(NodeTopicActivity.class)
                .putExtra(NodeTopicActivity.TAG_LINK_KEY, link)
                .start();
    }
}
