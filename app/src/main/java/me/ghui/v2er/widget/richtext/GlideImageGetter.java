package me.ghui.v2er.widget.richtext;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.orhanobut.logger.Logger;

import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.util.UriUtils;


/**
 * Created by ghui on 05/07/2017.
 */

public class GlideImageGetter implements Html.ImageGetter {

    private static String TAG = "ImgGetter";

    private TextView mTextView;
    private ImageHolder mImageHolder;

    public GlideImageGetter(TextView textView, ImageHolder imageHolder) {
        mTextView = textView;
        mImageHolder = imageHolder;
    }

    @Override
    public Drawable getDrawable(String source) {
        source = UriUtils.checkSchema(source);
        NetWorkDrawable drawable = new NetWorkDrawable(mImageHolder);
        GlideApp.with(mTextView)
                .load(source)
                .fitCenter()
                .into(new NetWorkDrawableTarget(mTextView, drawable, mImageHolder.maxSize));
        return drawable;
    }

    private static class NetWorkDrawable extends BitmapDrawable {
        private Drawable mDrawable;
        private ImageHolder mImageHolder;
        private boolean isFailed;

        public NetWorkDrawable(ImageHolder imageHolder) {
            mImageHolder = imageHolder;
            setBounds(mImageHolder.mLoadingDrawable.getBounds());
            Logger.d(TAG, "bounds: " + getBounds());
        }

        public void setFailed(boolean isFailed) {
            this.isFailed = isFailed;
        }

        public ImageHolder getImageHolder() {
            return mImageHolder;
        }

        public void setDrawable(Drawable drawable) {
            mDrawable = drawable;
            setBounds(mDrawable.getBounds());
        }

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) {
                mDrawable.draw(canvas);
                return;
            }

            if (isFailed) {
                mImageHolder.mLoadErrorDrawable.draw(canvas);
            } else {
                mImageHolder.mLoadingDrawable.draw(canvas);
            }
        }

    }

    private static class NetWorkDrawableTarget extends SimpleTarget<Drawable> {

        private TextView mTextView;
        private NetWorkDrawable mDrawable;

        public NetWorkDrawableTarget(TextView textView, NetWorkDrawable drawable, int maxWidth) {
            //这里只缩小不放大
            super(maxWidth, SIZE_ORIGINAL);
            mTextView = textView;
            mDrawable = drawable;
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
            int h = resource.getIntrinsicHeight();
            int w = resource.getIntrinsicWidth();
            Logger.e("onResourceReady: h=" + h + ", w=" + w);
            resource.setBounds(0, 0, w, h);
            mDrawable.setDrawable(resource);
            mTextView.setText(mTextView.getText());
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            mDrawable.setFailed(true);
            mTextView.setText(mTextView.getText());
            Logger.d(TAG, "onLoadFailed");
        }
    }

}
