package me.ghui.v2er.module.topic;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.Pref;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.richtext.OnUrlClickListener;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 09/05/2017.
 */

public class TopicReplyItemDelegate extends ItemViewDelegate<TopicInfo.Item> {

    private OnMemberClickListener mMemberClickListener;

    public TopicReplyItemDelegate(Context context) {
        super(context);
    }

    public void setMemberClickListener(OnMemberClickListener memberClickListener) {
        mMemberClickListener = memberClickListener;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.topic_reply_item;
    }

    @Override
    public boolean isForViewType(@Nullable TopicInfo.Item item, int position) {
        return item != null && !item.isHeaderItem() && !item.isContentItem() && !(item instanceof TopicInfo.ReplySortHeader);
    }

    @Override
    public void convert(ViewHolder holder, TopicInfo.Item item, int position) {
        TopicInfo.Reply replyInfo = (TopicInfo.Reply) item;
        GlideApp.with(mContext)
                .load(replyInfo.getAvatar())
                .placeholder(R.drawable.avatar_placeholder_drawable)
                .into((ImageView) holder.getView(R.id.reply_avatar_img));
        holder.getView(R.id.reply_user_tag_tv).setVisibility(replyInfo.isOwner() ? View.VISIBLE : View.GONE);
        if (replyInfo.isOwner() &&
                Pref.readBool(R.string.pref_key_highlight_topic_owner_reply_item)) {
            holder.getConvertView().setBackgroundColor(0X1A555555);
        } else {
            holder.getConvertView().setBackgroundColor(Color.TRANSPARENT);
        }
        holder.setText(R.id.reply_user_name_tv, replyInfo.getUserName());
        if (replyInfo.getLove() == 0) {
            holder.getView(R.id.reply_thx_tv).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.reply_thx_tv).setVisibility(View.VISIBLE);
            holder.setText(R.id.reply_thx_tv, replyInfo.getLove() + "");
        }
        ImageView img = holder.getView(R.id.reply_thx_img);
        img.setVisibility(View.VISIBLE);
        img.setImageResource(replyInfo.hadThanked() ? R.drawable.love_checked_icon : R.drawable.love_normal_icon);
        holder.setText(R.id.time_tv, replyInfo.getTime());
        TextView contentView = holder.getView(R.id.content_tv);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontSizeUtil.getContentSize());
        if (Check.notEmpty(replyInfo.getReplyContent())) {
            contentView.setVisibility(View.VISIBLE);
            contentView = holder.getView(R.id.content_tv);
            String replyContent = replyInfo.getReplyContent();
            OnMemberLinkClickListener clickListener = null;
            if (replyContent.contains("/member/")) {
                clickListener = new OnMemberLinkClickListener(holder.index());
            }
            RichText.from(replyContent)
                    .urlClick(clickListener)
                    .widthDelta(91)
                    .into(contentView);
        } else {
            contentView.setVisibility(View.GONE);
        }
        holder.setText(R.id.floor_tv, replyInfo.getFloor());
    }

    public interface OnMemberClickListener {
        void onMemberClick(String userName, int index);
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
            } else {
                Utils.openWap(url, mContext);
            }
            return false;
        }
    }

}
