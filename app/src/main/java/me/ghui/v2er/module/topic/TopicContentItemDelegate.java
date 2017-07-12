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
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.imgviewer.ImageViewer;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.AppendTopicContentView;
import me.ghui.v2er.widget.richtext.OnImageClickListener;
import me.ghui.v2er.widget.richtext.OnUrlClickListener;
import me.ghui.v2er.widget.richtext.RichText;

/**
 * Created by ghui on 09/05/2017.
 */

public class TopicContentItemDelegate extends ItemViewDelegate<TopicInfo.Item> implements OnImageClickListener, OnUrlClickListener {

    public TopicContentItemDelegate(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.topic_content_item;
    }

    @Override
    public boolean isForViewType(TopicInfo.Item item, int position) {
        return item != null && item.isContentItem();
    }

    @Override
    public void convert(ViewHolder holder, TopicInfo.Item item, int position) {
        TopicInfo.ContentInfo contentInfo = (TopicInfo.ContentInfo) item;
        TextView contentTv = holder.getView(R.id.content_tv);
        if (PreConditions.notEmpty(contentInfo.getContentHtml())) {
            contentTv.setVisibility(View.VISIBLE);
            RichText.from(contentInfo.getContentHtml())
                    .imgClick(this)
                    .urlClick(this)
                    .into(contentTv);
            contentTv.setTextIsSelectable(true);
        } else {
            contentTv.setVisibility(View.GONE);
        }
        ((AppendTopicContentView) holder.getView(R.id.append_topic_contentview))
                .setData(contentInfo.getPostScripts());
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
