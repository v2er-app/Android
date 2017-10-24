/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package me.ghui.v2er.module.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.concurrent.ThreadLocalRandom;

import me.ghui.v2er.util.TexureUtil;
import me.ghui.v2er.util.Voast;

public class GalleryImageView extends FrameLayout {
    final MultiTouchImageView imageView;
    final ProgressBar progressBar;
    private final SimpleTarget<Drawable> mTarget;

    // TODO: 24/10/2017 dark color 
    private int[] mRandomColor = {0xff04beed, 0xff532F87, 0xff04b33e, 0xff622C12, 0xff13646A, 0xff2a9bbd, 0xffffbd0f, 0xff99ae52, 0xff621230};

    public GalleryImageView(Context context) {
        this(context, new MultiTouchImageView(context), new ProgressBar(context));
    }

    GalleryImageView(Context context, MultiTouchImageView imageView, ProgressBar progressBar) {
        super(context);
        this.imageView = imageView;
        this.progressBar = progressBar;

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        progressBar.setLayoutParams(params);
        addView(progressBar);

        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        imageView.setLayoutParams(params);
        addView(imageView);

        int maxSize = TexureUtil.getMaxTextureSize();
        mTarget = new SimpleTarget<Drawable>(maxSize, maxSize) {
           
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                imageView.setImageResource(android.R.color.transparent);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
                if (resource instanceof GifDrawable) {
                    ((GifDrawable) resource).start();
                }
                progressBar.setVisibility(View.GONE);
                paletteBg(resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                Voast.show("图片加载出错");
            }
        };
    }

    private int getRandomColor() {
        int index = ThreadLocalRandom.current().nextInt(0, mRandomColor.length);
        return mRandomColor[index];
    }

    private void paletteBg(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof GifDrawable) {
            bitmap = ((GifDrawable) drawable).getFirstFrame();
        }
        if (bitmap == null) return;
        Palette.from(bitmap)
                .generate(palette -> {
                    //柔和色中的暗色
                    Palette.Swatch textSwatch = palette.getDarkMutedSwatch();
                    int color;
                    if (textSwatch == null) {
                        color = getRandomColor();
                    } else {
                        color = textSwatch.getRgb();
                    }
                    setBackgroundColor(color);
                });
    }

    public Target<Drawable> target() {
        return mTarget;
    }

    public void setSwipeToDismissCallback(SwipeToDismissTouchListener.Callback callback) {
        final OnTouchListener listener =
                SwipeToDismissTouchListener.createFromView(imageView, callback);
        imageView.setOnTouchListener(listener);
    }

}
