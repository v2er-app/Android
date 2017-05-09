package me.ghui.v2ex.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.ghui.v2ex.R;
import me.ghui.v2ex.network.bean.TopicInfo;
import me.ghui.v2ex.util.ScaleUtils;
import me.ghui.v2ex.util.Utils;

/**
 * Created by ghui on 07/05/2017.
 */

public class AppendTopicContentView extends LinearLayout {
    public AppendTopicContentView(Context context) {
        super(context);
        init();
    }

    public AppendTopicContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppendTopicContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setBackgroundColor(0xfffafafa);
    }

    public void setData(List<TopicInfo.HeaderInfo.PostScript> data) {
        if (Utils.isEmpty(data)) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            this.removeAllViews();
            for (TopicInfo.HeaderInfo.PostScript postScript : data) {
                addView(ItemView.create(postScript, getContext()));
            }
        }
    }


    private static class ItemView extends LinearLayout {

        private TextView headerTV;
        private TextView contentTV;

        private ItemView(Context context) {
            super(context);
            init(context);
        }

        private ItemView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        private ItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            setOrientation(VERTICAL);
            int padding = ScaleUtils.dp(8, context);
            setPadding(padding, padding, padding, padding);
            headerTV = new TextView(context);
            headerTV.setTextAppearance(context, R.style.hintText);
            addView(headerTV);
            contentTV = new TextView(context);
            contentTV.setTextAppearance(context, R.style.BodyText);
            addView(contentTV);
        }

        public static ItemView create(TopicInfo.HeaderInfo.PostScript post, Context context) {
            ItemView itemView = new ItemView(context);
            itemView.headerTV.setText(post.getHeader());
            itemView.contentTV.setText(post.getContent());
            return itemView;
        }


    }
}
