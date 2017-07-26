package me.ghui.v2er.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 26/07/2017.
 */

public class MentionedReplySheetDialog extends BottomSheetDialog {

    @BindView(R.id.mention_title_tv)
    TextView mTitleTv;
    @BindView(R.id.base_recyclerview)
    BaseRecyclerView mRecyclerView;


    public MentionedReplySheetDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public MentionedReplySheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
        init();
    }

    protected MentionedReplySheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
//        setCanceledOnTouchOutside(false);
        setContentView(R.layout.topic_mention_reply_layout);
        ButterKnife.bind(this);
        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new MentionAdapter(getContext(), R.layout.topic_mention_reply_item));
    }


    public void setData(List<TopicInfo.Reply> replies, String owner) {
        mTitleTv.setText(owner + "之前的回复");
        MentionAdapter mentionAdapter = (MentionAdapter) mRecyclerView.getAdapter();
        mentionAdapter.setData(replies);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.1f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        window.setAttributes(lp);
    }

    private static class MentionAdapter extends CommonAdapter<TopicInfo.Reply> {

        public MentionAdapter(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        protected void convert(ViewHolder holder, TopicInfo.Reply replyInfo, int position) {
            RichText.from(replyInfo.getReplyContent()).into(holder.getTextView(R.id.reply_content_tv));
            holder.setText(R.id.time_tv, replyInfo.getTime());
        }
    }

}
