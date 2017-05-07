package me.ghui.v2ex.module.topic;

import android.content.Context;

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
                super.convert(holder, reply, position);
                // TODO: 05/05/2017
            }
        });
    }

}
