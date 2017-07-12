package me.ghui.v2er.module.topic;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.imgviewer.ImageViewer;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.richtext.OnImageClickListener;
import me.ghui.v2er.widget.richtext.OnUrlClickListener;

/**
 * Created by ghui on 09/05/2017.
 */

public class TopicHeaderItemDelegate extends ItemViewDelegate<TopicInfo.Item> implements OnImageClickListener, OnUrlClickListener {

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
        ImageView avatarImg = holder.getImgView(R.id.avatar_img);
        if (avatarImg.getDrawable() == null) {
            Glide.with(mContext).load(headerInfo.getAvatar()).into(avatarImg);
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
        holder.setText(R.id.comment_num_tv, headerInfo.getCommentNum());
        holder.setText(R.id.topic_header_title_tv, headerInfo.getTitle());
    }

    @Override
    public void onImgClick(ImagesInfo imagesInfo) {
        ImageViewer.open(imagesInfo, mContext);
    }

    @Override
    public boolean onUrlClick(String url) {
        Utils.openWap(url, mContext);
        return false;
    }
}
