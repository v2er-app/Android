package me.ghui.v2er.module.topic;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import me.ghui.toolbox.android.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.FontSizeUtil;
import me.ghui.v2er.util.ViewUtils;

/**
 * Created by ghui on 09/05/2017.
 */

public class TopicHeaderItemDelegate extends ItemViewDelegate<TopicInfo.Item> {
    private float contentTextSize;

    public TopicHeaderItemDelegate(Context context) {
        super(context);
        contentTextSize = FontSizeUtil.getContentSize();
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
        ImageView avatarImg = holder.getImgView(R.id.avatar_img);
        if (avatarImg.getDrawable() == null) {
            Logger.d("NewsAvatar:4 " + headerInfo.getAvatar());
            GlideApp.with(mContext)
                    .load(headerInfo.getAvatar())
                    .placeholder(R.drawable.avatar_placeholder_drawable)
                    .into(avatarImg);
        }
        holder.setText(R.id.user_name_tv, headerInfo.getUserName());
        holder.setText(R.id.time_tv, headerInfo.getTime());
        holder.setText(R.id.tagview, headerInfo.getTag());

        TextView viewCountTv = holder.getView(R.id.view_count_tv);
        int viewCount = headerInfo.getViewCount();
        if (viewCount > 0) {
            viewCountTv.setVisibility(View.VISIBLE);
            viewCountTv.setText("点击" + viewCount);
        } else {
            viewCountTv.setVisibility(View.GONE);
        }
        TextView titleTv = holder.getTextView(R.id.topic_header_title_tv);
//        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);
        titleTv.setText(headerInfo.getTitle());
        TextView commentTV = holder.getTextView(R.id.comment_num_tv);
        String commentNum = headerInfo.getCommentNum();
        if (Check.isEmpty(commentNum)) {
            commentTV.setVisibility(View.GONE);
        } else {
            commentTV.setVisibility(View.VISIBLE);
            commentTV.setText("评论" + commentNum);
        }
        ViewUtils.highlightCommentNum(commentTV);
    }

}
