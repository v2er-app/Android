package me.ghui.v2er.module.topic;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import javax.annotation.Nullable;

import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.richtext.OnUrlClickListener;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 09/05/2017.
 */

public class TopicReplyItemDelegate extends ItemViewDelegate<TopicInfo.Item> {

    private OnMemberClickListener mMemberClickListener;

    public void setMemberClickListener(OnMemberClickListener memberClickListener) {
        mMemberClickListener = memberClickListener;
    }

    public TopicReplyItemDelegate(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.topic_reply_item;
    }

    @Override
    public boolean isForViewType(@Nullable TopicInfo.Item item, int position) {
        return item != null && !item.isHeaderItem() && !item.isContentItem();
    }

    @Override
    public void convert(ViewHolder holder, TopicInfo.Item item, int position) {
        TopicInfo.Reply replyInfo = (TopicInfo.Reply) item;
        Picasso.with(mContext)
                .load(replyInfo.getAvatar())
                .placeholder(R.drawable.avatar_placeholder_drawable)
                .into((ImageView) holder.getView(R.id.reply_avatar_img));
        holder.getView(R.id.reply_user_tag_tv).setVisibility(replyInfo.isOwner() ? View.VISIBLE : View.GONE);
        holder.setText(R.id.reply_user_name_tv, replyInfo.getUserName());
        if (replyInfo.getLove() == 0) {
            holder.getView(R.id.reply_thx_tv).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.reply_thx_tv).setVisibility(View.VISIBLE);
            holder.setText(R.id.reply_thx_tv, replyInfo.getLove() + "");
        }
        ImageView img = holder.getView(R.id.reply_thx_img);
        if (!item.isSelf()) {
            img.setVisibility(View.VISIBLE);
            img.setImageResource(replyInfo.hadThanked() ? R.drawable.love_checked_icon : R.drawable.love_normal_icon);
        } else {
            img.setVisibility(View.GONE);
        }
        holder.setText(R.id.time_tv, replyInfo.getTime());
        TextView contentView = holder.getView(R.id.content_tv);
        if (PreConditions.notEmpty(replyInfo.getReplyContent())) {
            contentView.setVisibility(View.VISIBLE);
            contentView.setTextIsSelectable(true);
            contentView = holder.getView(R.id.content_tv);
            String replyContent = replyInfo.getReplyContent();
            OnMemberLinkClickListener clickListener = null;
            if (replyContent.contains("/member/")) {
                clickListener = new OnMemberLinkClickListener(holder.index());
            }
            RichText.from(replyContent)
                    .urlClick(clickListener)
                    .into(contentView);
        } else {
            contentView.setVisibility(View.GONE);
        }
        holder.setText(R.id.floor_tv, replyInfo.getFloor());
    }

    private class OnMemberLinkClickListener implements OnUrlClickListener {
        private int clickIndex;

        public OnMemberLinkClickListener(int clickIndex) {
            this.clickIndex = clickIndex;
        }

        @Override
        public boolean onUrlClick(String url) {
            if (url.startsWith("/member/")) {
                String member = url.substring("/member/".length(), url.length());
                if (mMemberClickListener != null) {
                    mMemberClickListener.onMemberClick(member, clickIndex);
                }
                return true;
            }
            return false;
        }
    }

    public interface OnMemberClickListener {
        void onMemberClick(String userName, int index);
    }

}
