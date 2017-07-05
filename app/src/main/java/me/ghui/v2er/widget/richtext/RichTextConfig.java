package me.ghui.v2er.widget.richtext;

import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import me.ghui.v2er.general.PreConditions;


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

    public RichTextConfig maxSize(int maxSize) {
        if (mImageHolder == null) {
            mImageHolder = new ImageHolder();
        }
        mImageHolder.maxSize = maxSize;
        return this;
    }

    public void into(TextView textView) {
        if (mImageGetter == null && !noImg) {
            mImageGetter = new GlideImageGetter(textView, mImageHolder);
        }
        Spanned spanned = Html.fromHtml(sourceText, mImageGetter, mTagHandler);
        textView.setText(removeBottomPadding(spanned));
    }

    private CharSequence removeBottomPadding(CharSequence text) {
        if (PreConditions.isEmpty(text)) return null;
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

}
