package me.ghui.v2ex.general;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.orhanobut.logger.Logger;

/**
 * Created by ghui on 10/05/2017.
 */

public class GlideImageGetter implements Html.ImageGetter {
    // TODO: 10/05/2017  

    private TextView mTextView;

    public GlideImageGetter(TextView textView) {
        mTextView = textView;
    }

    @Override
    public Drawable getDrawable(String source) {
        DrawableWrapper drawableWrapper = new DrawableWrapper();
        Glide.with(mTextView.getContext()).load(source).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Logger.d("img loaded");
                drawableWrapper.mDrawable = new BitmapDrawable(mTextView.getResources(), resource);
                mTextView.invalidate();
                mTextView.setText(mTextView.getText());
            }
        });
        return drawableWrapper.mDrawable;
    }

    private static class DrawableWrapper {
        public Drawable mDrawable;
    }


}
