package me.ghui.v2er.module.imgur;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import me.ghui.v2er.R;
import me.ghui.v2er.adapter.base.CommonAdapter;
import me.ghui.v2er.adapter.base.ViewHolder;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.network.imgur.ImgurUploadHistory;

/**
 * Adapter for displaying uploaded images in a grid
 */
public class MyUploadsAdapter extends CommonAdapter<ImgurUploadHistory.UploadedImage> {

    public MyUploadsAdapter(Context context) {
        super(context, R.layout.item_upload_image);
    }

    @Override
    protected void convert(ViewHolder holder, ImgurUploadHistory.UploadedImage image, int position) {
        ImageView imageView = holder.getView(R.id.upload_image_iv);

        // Make the ImageView square
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int width = imageView.getWidth();
                if (width > 0) {
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = width;
                    imageView.setLayoutParams(params);
                }
                return true;
            }
        });

        // Load thumbnail image using GlideApp
        GlideApp.with(mContext)
                .load(image.getThumbnailLink())
                .placeholder(R.drawable.avatar_placeholder_drawable)
                .centerCrop()
                .into(imageView);
    }
}
