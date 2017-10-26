package me.ghui.v2er.module.gallery;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.util.Utils;

/**
 * Created by ghui on 22/10/2017.
 */

public class GalleryAdapter extends PagerAdapter {

    private ImagesInfo mImagesInfo;
    private Context mContext;
    private SwipeToDismissTouchListener.Callback mSwipeToDissmissListener;
    private View.OnClickListener mOnImageClickedListener;

    public GalleryAdapter(ImagesInfo imagesInfo, Context context) {
        mImagesInfo = imagesInfo;
        mContext = context;
    }

    public void setSwipeToDissmissListener(SwipeToDismissTouchListener.Callback swipeToDissmissListener) {
        mSwipeToDissmissListener = swipeToDissmissListener;
    }

    public void setOnImageClickedListener(View.OnClickListener clickedListener) {
        mOnImageClickedListener = clickedListener;
    }

    @Override
    public int getCount() {
        return mImagesInfo == null ? 0 : Utils.listSize(mImagesInfo.getImages());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private ImagesInfo.Images.Image getItem(int postion) {
        return mImagesInfo.getImages().get(postion);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final GalleryImageView root = new GalleryImageView(mContext);
        root.setSwipeToDismissCallback(mSwipeToDissmissListener);
        root.setOnImageClicked(mOnImageClickedListener);
        container.addView(root);
        GlideApp.with(mContext)
                .load(getItem(position).getUrl())
                .fitCenter()
                .into(root.target());
        return root;
    }

}
