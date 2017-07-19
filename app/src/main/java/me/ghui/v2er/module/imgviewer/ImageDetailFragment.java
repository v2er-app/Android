package me.ghui.v2er.module.imgviewer;

import android.graphics.Bitmap;
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

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import me.ghui.v2er.R;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.Voast;


/**
 * Created by ghui on 5/18/16.
 */
public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private SubsamplingScaleImageView mImageView;
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
        mImageView = (SubsamplingScaleImageView) view.findViewById(R.id.imageview);
        mImageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        mImageView.setOnClickListener(v -> getActivity().finish());
        loadImage();
    }

    private void loadImage() {
        Picasso.with(getContext())
                .load(mImageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mImageView.setImage(ImageSource.bitmap(bitmap));
                        progressBar.setVisibility(View.GONE);
                        paletteBg(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        progressBar.setVisibility(View.GONE);
                        Voast.show("图片加载出错");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
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
