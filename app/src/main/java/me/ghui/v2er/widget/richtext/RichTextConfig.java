package me.ghui.v2er.widget.richtext;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.widget.TextView;



import me.ghui.v2er.R;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.ViewUtils;


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
            if (mImageHolder == null) {
                mImageHolder = new ImageHolder();
                mImageHolder.maxSize = ViewUtils.getExactlyWidth(textView, true);
                if (mImageHolder.maxSize <= 0) {
                    mImageHolder.maxSize = (int) (ScaleUtils.getScreenW() - ScaleUtils.dp(32));
                }
            }
            mImageGetter = new GlideImageGetter(textView, mImageHolder);
        }
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(sourceText, mImageGetter, mTagHandler);
        CharSequence text = removePadding(spanned, textView);
        textView.setText(text);
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

}
