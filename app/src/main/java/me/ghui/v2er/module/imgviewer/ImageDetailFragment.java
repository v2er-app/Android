package me.ghui.v2er.module.imgviewer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.orhanobut.logger.Logger;

import me.ghui.v2er.R;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.Voast;


/**
 * Created by ghui on 5/18/16.
 */
public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private PhotoView mPhotoView;
    private ProgressBar progressBar;
    private View mImgRootView;
    private static String IMG_URL = Constants.PACKAGE_NAME + "_img_url";

    public static ImageDetailFragment newInstance(String url) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString(IMG_URL, url);
        Log.e("img", "url: " + url);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMG_URL) : null;
        mImageUrl = UriUtils.checkSchema(mImageUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImgRootView = view.findViewById(R.id.img_viewer_root);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        mPhotoView = (PhotoView) view.findViewById(R.id.imageview);
//        mPhotoView.setOnClickListener(v -> getActivity().finish());
//        PhotoViewAttacher picView = new PhotoViewAttacher(mPhotoView);
        loadImage();
    }

    private void loadImage() {
        GlideApp.with(getContext())
                .load(mImageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Voast.show("图片加载出错");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        mPhotoView.setImageDrawable(resource);
                        if (resource instanceof BitmapDrawable) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            paletteBg(bitmap);
                        }
                        return true;
                    }
                })
                .into(mPhotoView);


    }

    private void paletteBg(Bitmap bitmap) {
        Palette.from(bitmap)
                .generate(palette -> {
                    //柔和色中的暗色
                    Palette.Swatch textSwatch = palette.getDarkMutedSwatch();
                    if (textSwatch == null) {
                        Logger.e("textSwatch is null");
                        return;
                    }
                    mImgRootView.setBackgroundColor(textSwatch.getRgb());
//                    titleColorText.setTextColor(textSwatch.getTitleTextColor());
                });
    }


}
