package me.ghui.v2er.widget.richtext;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Collection;

import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.util.L;
import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.TexureUtil;
import me.ghui.v2er.util.UriUtils;


/**
 * Created by ghui on 05/07/2017.
 */

public class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {
    private final Collection<Target> mTargets = new ArrayList<>();
    private TextView mTextView;
    private Drawable mLoadingDrawable;
    private Drawable mErrorDrawable;
    private int mMaxSize;

    @SuppressLint("RestrictedApi")
    public GlideImageGetter(TextView textView, ImageHolder imageHolder) {
        mTextView = textView;
        mLoadingDrawable = imageHolder.mLoadingDrawable;
        mErrorDrawable = imageHolder.mLoadErrorDrawable;
        mMaxSize = imageHolder.maxSize;
    }

    @Override
    public Drawable getDrawable(String source) {
        source = UriUtils.checkSchema(source);
        if (!UriUtils.isValideUrl(source)) return null;
        WrapperTarget target = new WrapperTarget(mMaxSize);
        mTargets.add(target);
        GlideApp.with(mTextView)
                .load(source)
                .placeholder(mLoadingDrawable)
                .error(mErrorDrawable)
                .centerInside()
                .into(target);
        return target.getWrapperDrawable();
    }

    private void clearTarget(Target target) {
        mTargets.remove(target);
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        mTextView.setText(mTextView.getText());
        mTextView.invalidate();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
    }

    private class WrapperTarget extends SimpleTarget<Drawable> {
        private int mMaxWidth;
        private DrawableWrapper wrapperDrawable;

        @SuppressLint("RestrictedApi")
        public WrapperTarget(int maxWidth) {
            //这里只缩小不放大
            super(maxWidth, TexureUtil.fitMaxHeight());
            this.mMaxWidth = maxWidth;
            wrapperDrawable = new DrawableWrapper(null);
            wrapperDrawable.setCallback(GlideImageGetter.this);
            updateWrapperedDrawable(mLoadingDrawable);
        }

        public DrawableWrapper getWrapperDrawable() {
            return wrapperDrawable;
        }

        @Override
        public void onLoadStarted(@Nullable Drawable placeholder) {
            updateWrapperedDrawable(placeholder);
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
            L.e( "onResourceReady----------");
            clearTarget(this);
            int h = resource.getIntrinsicHeight();
            int w = resource.getIntrinsicWidth();
            float scaleW = Math.min(ScaleUtils.dp(w), mMaxWidth);
            float scaleH = h * (scaleW / w);
            resource.setBounds(0, 0, (int) scaleW, (int) scaleH);
            updateWrapperedDrawable(resource);
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            clearTarget(this);
            updateWrapperedDrawable(errorDrawable);
        }

        @SuppressLint("RestrictedApi")
        public void updateWrapperedDrawable(Drawable drawable) {
            if (drawable instanceof GifDrawable) {
                ((GifDrawable) drawable).setLoopCount(GifDrawable.LOOP_FOREVER);
                ((GifDrawable) drawable).start();
            }
            wrapperDrawable.setWrappedDrawable(drawable);
            wrapperDrawable.setBounds(drawable.getBounds());
            wrapperDrawable.invalidateSelf();
        }
    }

}
