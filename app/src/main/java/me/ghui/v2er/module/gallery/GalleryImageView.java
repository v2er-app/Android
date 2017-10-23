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
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

public class GalleryImageView extends FrameLayout {
    final MultiTouchImageView imageView;
    final ProgressBar progressBar;
    private final SimpleTarget<Drawable> mTarget;

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

        int maxSize = Utils.getMaxTextureSize();
        mTarget = new SimpleTarget<Drawable>(maxSize, SIZE_ORIGINAL) {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                imageView.setImageResource(android.R.color.transparent);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
                if(resource instanceof GifDrawable){
                    ((GifDrawable) resource).start();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                progressBar.setVisibility(View.GONE);
                Voast.show("图片加载出错");
            }
        };
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
