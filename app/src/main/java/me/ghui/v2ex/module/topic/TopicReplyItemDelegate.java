package me.ghui.v2ex.module.topic;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import javax.annotation.Nullable;

import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.ItemViewDelegate;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.network.bean.TopicInfo;

/**
 * Created by ghui on 09/05/2017.
 */

public class TopicReplyItemDelegate extends ItemViewDelegate<TopicInfo.Item> {

    public TopicReplyItemDelegate(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.topic_reply_item;
    }

    @Override
    public boolean isForViewType(@Nullable TopicInfo.Item item, int position) {
        return item != null && !item.isHeaderItem();
    }

    @Override
    public void convert(ViewHolder holder, TopicInfo.Item item, int position) {
        TopicInfo.Reply replyInfo = (TopicInfo.Reply) item;
        Glide.with(mContext)
                .load("https:" + replyInfo.getAvatar())
                .into((ImageView) holder.getView(R.id.avatar_img));
        holder.setText(R.id.user_name_tv, replyInfo.getUserName());
        holder.setText(R.id.time_tv, replyInfo.getTime());
        holder.setText(R.id.content_tv, replyInfo.getReplyContent());
        holder.setText(R.id.floor_tv, replyInfo.getFloor());
    }
}
