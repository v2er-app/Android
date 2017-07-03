package me.ghui.v2er.module.topic;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.ItemViewDelegate;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.widget.AppendTopicContentView;

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
        ImageView avatarImg = holder.getImgView(R.id.avatar_img);
        if (avatarImg.getDrawable() == null) {
            Glide.with(mContext)
                    .load(headerInfo.getAvatar())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            ((BaseActivity) mContext).scheduleStartPostponedTransition(holder.getImgView(R.id.avatar_img));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            ((BaseActivity) mContext).scheduleStartPostponedTransition(holder.getImgView(R.id.avatar_img));
                            return false;
                        }
                    }).into(avatarImg);
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
        holder.setText(R.id.title_tv, headerInfo.getTitle());
        if (PreConditions.notEmpty(headerInfo.getContentHtml())) {
            holder.getView(R.id.content_tv).setVisibility(View.VISIBLE);
//            RichText.fromHtml(headerInfo.getContentHtml()).into(holder.getView(R.id.content_tv));
            HtmlTextView htmlTextView = holder.getView(R.id.content_tv);
            htmlTextView.setHtml(headerInfo.getContentHtml(), new HtmlHttpImageGetter(htmlTextView));
        } else {
            holder.getView(R.id.content_tv).setVisibility(View.GONE);
        }
        ((AppendTopicContentView) holder.getView(R.id.append_topic_contentview)).setData(headerInfo.getPostScripts());
    }

}
