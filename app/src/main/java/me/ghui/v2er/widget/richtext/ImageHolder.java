package me.ghui.v2er.widget.richtext;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.orhanobut.logger.Logger;

import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.ViewUtils;

/**
 * Created by ghui on 05/07/2017.
 */

public class ImageHolder {
    //width
    public int maxSize;
    public Drawable mLoadingDrawable;
    public Drawable mLoadErrorDrawable;

    public ImageHolder(View view, int maxSize, Drawable loadingDrawable, Drawable loadErrorDrawable) {
        if (maxSize <= 0) {
            maxSize = ViewUtils.getExactlyWidth(view, true);
            if (maxSize <= 0) {
                maxSize = ScaleUtils.getScreenW() - ScaleUtils.dp(40);
            }
        }
        this.maxSize = maxSize;
//        L.e("maxSize: " + maxSize);
        if (loadingDrawable == null) {
            mLoadingDrawable = new ImageHolderDrawable(maxSize, view.getContext());
            ((ImageHolderDrawable) (mLoadingDrawable)).setText("加载中...");
        } else {
            mLoadingDrawable = loadingDrawable;
        }
        if (loadErrorDrawable == null) {
            mLoadErrorDrawable = new ImageHolderDrawable(maxSize, view.getContext());
            ((ImageHolderDrawable) (mLoadErrorDrawable)).setText("加载失败");
        } else {
            mLoadErrorDrawable = loadErrorDrawable;
        }
    }

}
