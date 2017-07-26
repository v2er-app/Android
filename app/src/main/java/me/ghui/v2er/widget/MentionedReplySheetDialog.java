package me.ghui.v2er.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.module.topic.TopicReplyItemDelegate;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 26/07/2017.
 */

public class MentionedReplySheetDialog extends BottomSheetDialog {
    private static MentionedReplySheetDialog sSheetDialog;

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
        setContentView(R.layout.topic_mention_reply_layout);
        ButterKnife.bind(this);
        mRecyclerView.addDivider();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new MentionAdapter(getContext(), R.layout.topic_mention_reply_item));
    }

    public static void show(List<TopicInfo.Reply> replies, String owner, Context context) {
        if (sSheetDialog == null) {
            sSheetDialog = new MentionedReplySheetDialog(context);
        }
        sSheetDialog.mTitleTv.setText(owner + "之前的回复");
        MentionAdapter mentionAdapter = (MentionAdapter) sSheetDialog.mRecyclerView.getAdapter();
        mentionAdapter.setData(replies);
        sSheetDialog.show();
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
