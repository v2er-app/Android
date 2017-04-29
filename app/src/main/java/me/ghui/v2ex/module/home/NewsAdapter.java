package me.ghui.v2ex.module.home;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.ItemViewDelegate;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.network.bean.NewsItem;
import me.ghui.v2ex.widget.LoadMoreRecyclerView;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsAdapter extends LoadMoreRecyclerView.Adapter<NewsItem> {

    public NewsAdapter(Context context) {
        super(context);
        addItemViewDelegate(new ItemViewDelegate<NewsItem>() {

            @Override
            public int getItemViewLayoutId() {
                return R.layout.common_list_item;
            }

            @Override
            public boolean isForViewType(NewsItem item, int position) {
                return position < getItemCount() - 1;
            }

            @Override
            public void convert(ViewHolder holder, NewsItem newsItem, int position) {
                Glide.with(mContext)
                        .load("https:" + newsItem.getAvatar())
                        .into((ImageView) holder.getView(R.id.avtar_img));
                holder.setText(R.id.username_tv, newsItem.getUser());
                holder.setText(R.id.tagview, newsItem.getTagName());
                holder.setText(R.id.title_tv, newsItem.getTitle());
                holder.setText(R.id.comment_num_tv, "评论" + newsItem.getReplies());
            }
        });
    }

}
