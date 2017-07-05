package me.ghui.v2er.widget.richtext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import me.ghui.v2er.util.ViewUtils;


/**
 * Created by ghui on 05/07/2017.
 */

public class GlideImageGetter implements Html.ImageGetter {

    private TextView mTextView;
    private ImageHolder mImageHolder;

    public GlideImageGetter(TextView textView, ImageHolder imageHolder) {
        mTextView = textView;
        mImageHolder = imageHolder;
    }

    @Override
    public Drawable getDrawable(String source) {
        NetWorkDrawable drawable = new NetWorkDrawable(mImageHolder);
        Glide.with(mTextView.getContext())
                .load(source).asBitmap().
                fitCenter()
                .into(new NetWorkDrawableTarget(mTextView, drawable));
        return drawable;
    }

    private static class NetWorkDrawable extends BitmapDrawable {

        private Drawable mDrawable;
        private ImageHolder mImageHolder;

        public NetWorkDrawable(ImageHolder imageHolder) {
            mImageHolder = imageHolder;
        }

        public ImageHolder getImageHolder() {
            return mImageHolder;
        }

        public void setDrawable(Drawable drawable) {
            mDrawable = drawable;
        }

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) {
                mDrawable.draw(canvas);
                return;
            }
        }
    }

    private static class NetWorkDrawableTarget extends SimpleTarget<Bitmap> {
        private TextView mTextView;
        private NetWorkDrawable mDrawable;
        private ImageHolder mImageHolder;

        public NetWorkDrawableTarget(TextView textView, NetWorkDrawable drawable) {
            mTextView = textView;
            mDrawable = drawable;
            mImageHolder = drawable.getImageHolder();
        }

        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(mTextView.getResources(), bitmap);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (mImageHolder == null) {
                mImageHolder = new ImageHolder();
                mImageHolder.maxSize = ViewUtils.getExactlyWidth(mTextView, true);
            }
            if (width < mImageHolder.maxSize) {
                height *= mImageHolder.maxSize / width;
                width = mImageHolder.maxSize;
            }
            bitmapDrawable.setBounds(0, 0, width, height);
            mDrawable.setDrawable(bitmapDrawable);
            mTextView.setText(mTextView.getText());
        }

    }

}
