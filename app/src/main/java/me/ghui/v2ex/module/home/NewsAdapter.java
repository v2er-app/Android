package me.ghui.v2ex.module.home;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.ItemViewDelegate;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.network.bean.NewsInfo;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsAdapter extends LoadMoreRecyclerView.Adapter<NewsInfo.Item> {

    public NewsAdapter(Context context) {
        super(context);
        addItemViewDelegate(new ItemViewDelegate<NewsInfo.Item>() {

            @Override
            public int getItemViewLayoutId() {
                return R.layout.common_list_item;
            }

            @Override
            public boolean isForViewType(NewsInfo.Item item, int position) {
                return position < getItemCount() - 1;
            }

            @Override
            public void convert(ViewHolder holder, NewsInfo.Item item, int position) {
                Glide.with(mContext)
                        .load("https:" + item.getAvatar())
                        .into((ImageView) holder.getView(R.id.avatar_img));
                holder.setText(R.id.user_name_tv, item.getUser());
                holder.setText(R.id.time_tv, item.getTime());
                holder.setText(R.id.tagview, item.getTagName());
                holder.setText(R.id.title_tv, item.getTitle());
                holder.setText(R.id.comment_num_tv, "评论" + item.getReplies());
            }
        });
    }

}
