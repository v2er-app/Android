package me.ghui.v2er.module.gallery;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.Window;

import butterknife.BindView;
import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.util.LightStatusBarUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.BaseToolBar;

/**
 * Created by ghui on 22/10/2017.
 */

public class GalleryActivity extends BaseActivity implements SwipeToDismissTouchListener.Callback {

    public static final String EXTRA_IMG_DATA = Utils.KEY("extra_img_data");
    @BindView(R.id.gallery_viewpager)
    ViewPager mViewPager;

    public static void open(ImagesInfo imgsData, Context context) {
        Navigator.from(context)
                .putExtra(EXTRA_IMG_DATA, imgsData)
                .to(GalleryActivity.class)
                .start();
    }

    @Override
    protected int pageColor() {
        return Color.BLACK;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_gallery;
    }

    @Override
    protected boolean supportSlideBack() {
        return false;
    }

    @Override
    protected void configSystemBars(Window window) {
        Utils.transparentBars(window, Color.TRANSPARENT, Color.TRANSPARENT);
        LightStatusBarUtils.setLightStatusBar(window, false);
    }

    @Override
    protected BaseToolBar attachToolbar() {
        return null;
    }

    @Override
    protected void init() {
        ImagesInfo imgData = (ImagesInfo) getIntent().getSerializableExtra(EXTRA_IMG_DATA);
        GalleryAdapter galleryAdapter = new GalleryAdapter(imgData, this);
        galleryAdapter.setSwipeToDissmissListener(this);
        mViewPager.setAdapter(galleryAdapter);
        mViewPager.setCurrentItem(imgData.getPosition());
    }

    @Override
    public void onDismiss() {
        finish();
    }

    @Override
    public void onMove(float translationY) {
    }

}
