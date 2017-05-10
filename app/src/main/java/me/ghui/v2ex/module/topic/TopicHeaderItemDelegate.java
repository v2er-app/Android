package me.ghui.v2ex.module.topic;

import android.content.Context;
import android.text.Html;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.ItemViewDelegate;
import me.ghui.v2ex.adapter.base.ViewHolder;
import me.ghui.v2ex.general.GlideImageGetter;
import me.ghui.v2ex.network.bean.TopicInfo;
import me.ghui.v2ex.widget.AppendTopicContentView;

/**
 * Created by ghui on 09/05/2017.
 */

public class TopicHeaderItemDelegate extends ItemViewDelegate<TopicInfo.Item> {

    public TopicHeaderItemDelegate(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.topic_header_item;
    }

    @Override
    public boolean isForViewType(TopicInfo.Item item, int position) {
        return item != null && item.isHeaderItem();
    }

    @Override
    public void convert(ViewHolder holder, TopicInfo.Item item, int position) {
        TopicInfo.HeaderInfo headerInfo = (TopicInfo.HeaderInfo) item;
        Glide.with(mContext)
                .load("https:" + headerInfo.getAvatar())
                .into((ImageView) holder.getView(R.id.avatar_img));
        holder.setText(R.id.user_name_tv, headerInfo.getUserName());
        holder.setText(R.id.time_tv, headerInfo.getTime());
        holder.setText(R.id.tagview, headerInfo.getTag());
        holder.setText(R.id.view_count_tv, "点击" + headerInfo.getViewCount());
        holder.setText(R.id.comment_num_tv, headerInfo.getCommentNum());
        holder.setText(R.id.title_tv, headerInfo.getTitle());
        holder.setText(R.id.content_tv,
                Html.fromHtml(headerInfo.getContentHtml(),
                        new GlideImageGetter(holder.getView(R.id.content_tv)), null));
        ((AppendTopicContentView) holder.getView(R.id.append_topic_contentview)).setData(headerInfo.getPostScripts());
    }

}
