package me.ghui.v2ex.module.topic;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.ItemViewDelegate;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.network.bean.TopicInfo;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 05/05/2017.
 */

public class TopicReplyListAdapter extends LoadMoreRecyclerView.Adapter<TopicInfo.Reply> {

    public TopicReplyListAdapter(Context context) {
        super(context);

        addItemViewDelegate(new ItemViewDelegate<TopicInfo.Reply>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.reply_list_item;
            }

            @Override
            public boolean isForViewType(TopicInfo.Reply item, int position) {
                return position < getItemCount() - 1;
            }

            @Override
            public void convert(ViewHolder holder, TopicInfo.Reply reply, int position) {
                Glide.with(mContext)
                        .load("https:" + reply.getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, reply.getUserName());
                holder.setText(R.id.time_tv, reply.getTime());
                holder.setText(R.id.content_tv, reply.getReplyContent());
            }
        });
    }

}
