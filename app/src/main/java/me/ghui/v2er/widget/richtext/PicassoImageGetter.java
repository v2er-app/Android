package me.ghui.v2er.widget.richtext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import me.ghui.v2er.util.ScaleUtils;
import me.ghui.v2er.util.ViewUtils;


/**
 * Created by ghui on 05/07/2017.
 */

public class PicassoImageGetter implements Html.ImageGetter {

    private static String TAG = "ImgGetter";

    private TextView mTextView;
    private ImageHolder mImageHolder;

    public PicassoImageGetter(TextView textView, ImageHolder imageHolder) {
        mTextView = textView;
        mImageHolder = imageHolder;
    }

    @Override
    public Drawable getDrawable(String source) {
        NetWorkDrawable drawable = new NetWorkDrawable(mImageHolder);
        Picasso.with(mTextView.getContext())
                .load(source)
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

    private static class NetWorkDrawableTarget implements Target {
        private TextView mTextView;
        private NetWorkDrawable mDrawable;
        private float maxWidth;

        public NetWorkDrawableTarget(TextView textView, NetWorkDrawable drawable, int maxWidth) {
            this.maxWidth = maxWidth;
            mTextView = textView;
            mDrawable = drawable;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d(TAG, "onBitmapLoaded" + from.toString());
            mDrawable.setFailed(false);
            int width = ScaleUtils.dp(bitmap.getWidth());
            int height = ScaleUtils.dp(bitmap.getHeight());
            Logger.d("bitmap.size: " + "w:" + width + ", h:" + height);
            int textviewWidth = ViewUtils.getExactlyWidth(mTextView, true);
            maxWidth = Math.max(maxWidth, textviewWidth);
            if (width > maxWidth) {
                height = (int) (height * (maxWidth / width));
                width = (int) maxWidth;
            }
            BitmapDrawable bitmapDrawable = new BitmapDrawable(mTextView.getResources(), bitmap);
            bitmapDrawable.setBounds(0, 0, width, height);
            mDrawable.setDrawable(bitmapDrawable);
            mTextView.setText(mTextView.getText());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mDrawable.setFailed(true);
            mTextView.setText(mTextView.getText());
            Log.d(TAG, "onLoadFailed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d(TAG, "onPrepareLoad");
        }
    }

}
