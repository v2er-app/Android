package me.ghui.v2er.module.gallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import butterknife.BindView;
import io.reactivex.Observable;
import me.ghui.v2er.util.Check;
import me.ghui.v2er.R;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.GlideRequest;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.network.BaseConsumer;
import me.ghui.v2er.util.FileUtils;
import me.ghui.v2er.util.RxUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.BaseToolBar;

/**
 * Created by ghui on 22/10/2017.
 */

public class GalleryActivity extends BaseActivity implements SwipeToDismissTouchListener.Callback, Toolbar.OnMenuItemClickListener {
    public static final String EXTRA_IMG_DATA = Utils.KEY("extra_img_data");
    @BindView(R.id.gallery_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.gallery_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.indicator_tv)
    TextView mIndicatorTv;
    private ImagesInfo mData;

    public static void open(ImagesInfo imgsData, Context context) {
        // Validate images data before opening gallery
        if (imgsData == null || imgsData.getImages() == null || imgsData.getImages().isEmpty()) {
            Voast.show("图片不存在");
            return;
        }

        // Check if at least one valid image exists
        boolean hasValidImage = false;
        for (ImagesInfo.Images.Image img : imgsData.getImages()) {
            if (img != null && img.getUrl() != null) {
                hasValidImage = true;
                break;
            }
        }

        if (!hasValidImage) {
            Voast.show("图片不存在");
            return;
        }

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
    protected void initTheme() {
//        super.initTheme();
    }

    @Override
    protected void reloadMode(int mode) {
        // do nothing
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
    protected BaseToolBar attachToolbar() {
        return null;
    }

    @Override
    protected void init() {
        Utils.setPaddingForStatusBar(mToolBar);
        mToolBar.inflateMenu(R.menu.gallery_toolbar_menu);
        mToolBar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert_white));
        mToolBar.setOnMenuItemClickListener(this);
        mToolBar.setNavigationOnClickListener(v -> GalleryActivity.this.onBackPressed());

        mData = (ImagesInfo) getIntent().getSerializableExtra(EXTRA_IMG_DATA);
        GalleryAdapter galleryAdapter = new GalleryAdapter(mData, this);
        galleryAdapter.setSwipeToDissmissListener(this);
        galleryAdapter.setOnImageClickedListener(v -> {
            if (mToolBar.getVisibility() == View.VISIBLE) {
                mToolBar.animate().alpha(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mToolBar.setVisibility(View.GONE);
                                Utils.fullScreen(getWindow(), true);
                            }
                        })
                        .start();
            } else {
                mToolBar.setVisibility(View.VISIBLE);
                mToolBar.animate().alpha(1).setListener(null).start();
                Utils.fullScreen(getWindow(), false);
            }
        });
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(galleryAdapter);
        mViewPager.setCurrentItem(mData.getPosition());
        mIndicatorTv.setText(mData.getPosition() + 1 + "/" + galleryAdapter.getCount());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mIndicatorTv.setText(position + 1 + "/" + galleryAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private String getCurrentImage() {
        int index = mViewPager.getCurrentItem();
        if (mData == null || mData.getImages() == null ||
            index < 0 || index >= mData.getImages().size() ||
            mData.getImages().get(index) == null) {
            return null;
        }
        return mData.getImages().get(index).getUrl();
    }

    private void saveImage() {
        String currentImage = getCurrentImage();
        if (currentImage == null) {
            Voast.show("图片不存在");
            return;
        }
        // Already have permission, do the thing
        glideRequest(currentImage).into(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File file, Transition<? super File> transition) {
                if (!FileUtils.isExternalStorageWritable()) {
                    Voast.show("存储空间不可用");
                    return;
                }
                Observable.just(file)
                        .compose(RxUtils.io_main())
                        .map(f -> FileUtils.saveImg(f, Utils.getTypeFromImgUrl(currentImage)))
                        .subscribe(new BaseConsumer<String>() {
                            @Override
                            public void onConsume(String path) {
                                if (Check.notEmpty(path)) {
                                    MediaScannerConnection.scanFile(GalleryActivity.this, new String[]{path}, null, null);
                                    Voast.show("保存成功");
                                } else {
                                    Voast.show("保存失败");
                                }
                            }
                        });
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String currentImage = getCurrentImage();
        if (currentImage == null) {
            Voast.show("图片不存在");
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_open_in_browser:
                Utils.openInBrowser(currentImage, this);
                break;
            case R.id.action_save:
                //check write external permission then do save stuff
                saveImage();
                break;
            case R.id.action_share:
                glideRequest(currentImage).into(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File file, Transition<? super File> transition) {
                        Utils.shareImg(file, Utils.getTypeFromImgUrl(currentImage), GalleryActivity.this);
                    }
                });
                break;
        }
        return false;
    }

    private GlideRequest<File> glideRequest(String imgUrl) {
        return GlideApp.with(this)
                .downloadOnly()
                .load(imgUrl)
                .dontTransform()
                .apply(new RequestOptions().onlyRetrieveFromCache(true));
    }

    @Override
    public void onDismiss() {
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    public void onMove(float translationY) {
    }


}
