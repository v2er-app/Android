package me.ghui.v2er.widget.richtext;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.widget.TextView;


import me.ghui.v2er.R;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.network.APIService;


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

    private CharSequence removePadding(SpannableStringBuilder text, TextView textView) {
        if (PreConditions.isEmpty(text)) return null;
        if (PreConditions.notEmpty(text) && text.charAt(0) == 65532) {
            int paddingTop = -(int) (3 * textView.getResources().getDimension(R.dimen.smallTextSize));
            textView.setPadding(textView.getPaddingLeft(), paddingTop,
                    textView.getPaddingRight(), textView.getPaddingBottom());
        }
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.delete(text.length() - 1, text.length());
        }
        return text;
    }

    public void into(TextView textView) {
        if (!noImg && mImageGetter == null) {
            mImageHolder = new ImageHolder(textView, maxSize, mLoadingDrawable, mLoaderrorDrawable);
            mImageGetter = new GlideImageGetter(textView, mImageHolder);
        }
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(sourceText, mImageGetter, mTagHandler);
        CharSequence content = removePadding(spanned, textView);
        textView.setText(content);
        ImagesInfo.Images images = APIService.fruit().fromHtml(sourceText, ImagesInfo.Images.class);
        textView.setMovementMethod(new HtmlMovementMethod(mUrlClickListener, mImageClickListener, images, textView));
    }

}
