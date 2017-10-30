package me.ghui.v2er.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 07/05/2017.
 */

public class AppendTopicContentView extends LinearLayout {
    // TODO: 26/07/2017 偶尔不显示
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
        setDividerDrawable(getResources().getDrawable(R.drawable.common_divider));
        setShowDividers(SHOW_DIVIDER_MIDDLE);
    }

    public void setData(List<TopicInfo.ContentInfo.PostScript> data) {
        if (Check.isEmpty(data)) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            this.removeAllViews();
            for (TopicInfo.ContentInfo.PostScript postScript : data) {
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
            int padding = ScaleUtils.dp(12, context);
            setPadding(padding, padding, padding, padding);
            headerTV = new TextView(context);
            headerTV.setTextAppearance(context, R.style.hintText);
            headerTV.setPadding(headerTV.getPaddingLeft(), headerTV.getPaddingTop(), headerTV.getPaddingRight(), ScaleUtils.dp(4));
            addView(headerTV);
            contentTV = new TextView(context);
//            contentTV.setTextIsSelectable(true);
            contentTV.setTextAppearance(context, R.style.BodyText);
            contentTV.setLineSpacing(0, 1.20f);
            addView(contentTV);
        }

        public static ItemView create(TopicInfo.ContentInfo.PostScript post, Context context) {
            ItemView itemView = new ItemView(context);
            itemView.headerTV.setText(post.getHeader());
            RichText.from(post.getContent())
                    .widthDelta(24)
                    .into(itemView.contentTV);
            return itemView;
        }
    }
}
