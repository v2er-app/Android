package me.ghui.v2er.widget.richtext;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import me.ghui.v2er.general.Vtml;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.network.APIService;
import me.ghui.v2er.util.ScaleUtils;


/**
 * Created by ghui on 05/07/2017.
 */

public class RichTextConfig {
    private String sourceText;
    private Html.ImageGetter mImageGetter;
    private Html.TagHandler mTagHandler;
    private OnUrlClickListener mUrlClickListener;
    private OnImageClickListener mImageClickListener;
    private boolean noImg = false;
    private ImageHolder mImageHolder;
    private int maxSize;
    private Drawable mLoadingDrawable;
    private Drawable mLoaderrorDrawable;
    private boolean mSupportUrlClick = true;

    public RichTextConfig(String sourceText) {
        this.sourceText = sourceText;
    }

    public RichTextConfig urlClick(OnUrlClickListener urlClickListener) {
        this.mUrlClickListener = urlClickListener;
        return this;
    }

    public RichTextConfig imgClick(OnImageClickListener imageClickListener) {
        this.mImageClickListener = imageClickListener;
        return this;
    }

    public RichTextConfig imageGetter(Html.ImageGetter imageGetter) {
        this.mImageGetter = imageGetter;
        return this;
    }

    public RichTextConfig noImg(boolean noImg) {
        this.noImg = noImg;
        return this;
    }

    public RichTextConfig loadingPlaceHolder(Drawable loadingDrawable) {
        this.mLoadingDrawable = loadingDrawable;
        return this;
    }

    public RichTextConfig loadErrorPlaceHolder(Drawable loadErrorDrawable) {
        this.mLoaderrorDrawable = loadErrorDrawable;
        return this;
    }

    public RichTextConfig maxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public RichTextConfig widthDelta(int deltaDp) {
        this.maxSize = ScaleUtils.getScreenW() - ScaleUtils.dp(deltaDp);
        return this;
    }

    public RichTextConfig supportUrlClick(boolean supportUrlClick) {
        this.mSupportUrlClick = supportUrlClick;
        return this;
    }

    public void into(TextView textView) {
        if (!noImg && mImageGetter == null) {
            mImageHolder = new ImageHolder(textView, maxSize, mLoadingDrawable, mLoaderrorDrawable);
            mImageGetter = new GlideImageGetter(textView, mImageHolder);
        }
        if (sourceText == null) sourceText = "";
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(sourceText, mImageGetter, mTagHandler);
        CharSequence content = Vtml.removePadding(spanned);
        textView.setText(content);
        ImagesInfo.Images images = APIService.fruit().fromHtml(sourceText, ImagesInfo.Images.class);
        if (mSupportUrlClick) {
            textView.setMovementMethod(new HtmlMovementMethod(mUrlClickListener, mImageClickListener, images, textView));
        }
    }

}
