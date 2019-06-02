package me.ghui.v2er.module.gallery;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import butterknife.BindView;
import io.reactivex.Observable;
import me.ghui.toolbox.android.Check;
import me.ghui.v2er.BuildConfig;
import me.ghui.v2er.R;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.general.GlideRequest;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.imgviewer.ImagesInfo;
import me.ghui.v2er.util.DayNightUtil;
import me.ghui.v2er.util.FileUtils;
import me.ghui.v2er.util.RxUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;
import me.ghui.v2er.widget.BaseToolBar;
import me.ghui.v2er.widget.dialog.BaseDialog;
import me.ghui.v2er.widget.dialog.ConfirmDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ghui on 22/10/2017.
 */

public class GalleryActivity extends BaseActivity implements SwipeToDismissTouchListener.Callback, Toolbar.OnMenuItemClickListener {

    public static final String EXTRA_IMG_DATA = Utils.KEY("extra_img_data");
    private static final int RC_WRITE_EXTERNAL_STORAGE = 1;

    @BindView(R.id.gallery_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.gallery_toolbar)
    Toolbar mToolBar;
    @BindView(R.id.indicator_tv)
    TextView mIndicatorTv;
    private ImagesInfo mData;

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
    protected void initTheme() {
//        super.initTheme();
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
        Utils.transparentBars(window, Color.TRANSPARENT, Color.TRANSPARENT, !DayNightUtil.isNightMode());
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
                configSystemBars(getWindow());
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
        return mData.getImages().get(index).getUrl();
    }

    @AfterPermissionGranted(RC_WRITE_EXTERNAL_STORAGE)
    private void requestWritePermission() {
        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (EasyPermissions.hasPermissions(this, perm)) {
            // Already have permission, do the thing
            glideRequest(getCurrentImage()).into(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(File file, Transition<? super File> transition) {
                    if (!FileUtils.isExternalStorageWritable()) {
                        Voast.show("存储空间不可用");
                        return;
                    }
                    Observable.just(file)
                            .compose(RxUtils.io_main())
                            .map(f -> FileUtils.saveImg(f, Utils.getTypeFromImgUrl(getCurrentImage())))
                            .subscribe(path -> {
                                if (Check.notEmpty(path)) {
                                    MediaScannerConnection.scanFile(GalleryActivity.this, new String[]{path}, null, null);
                                    Voast.show("保存成功");
                                } else {
                                    Voast.show("保存失败");
                                }
                            });
                }
            });
        } else {
            if (EasyPermissions.permissionPermanentlyDenied(GalleryActivity.this, perm)) {
                new ConfirmDialog.Builder(getActivity())
                        .title("无法继续")
                        .msg("请打开并允许" + getString(R.string.app_name) + "使用存储权限")
                        .negativeText("取消")
                        .positiveText("去设置", new BaseDialog.OnDialogClickListener() {
                            @Override
                            public void onClick(BaseDialog dialog) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                startActivityForResult(intent, AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE);
                            }
                        }).build().show();
            } else {
                // Do not have permissions, request them now
                EasyPermissions.requestPermissions(this, "保存图片需要存储权限",
                        RC_WRITE_EXTERNAL_STORAGE, perm);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            requestWritePermission();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_in_browser:
                Utils.openInBrowser(getCurrentImage(), this);
                break;
            case R.id.action_save:
                //check write external permission then do save stuff
                requestWritePermission();
                break;
            case R.id.action_share:
                glideRequest(getCurrentImage()).into(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File file, Transition<? super File> transition) {
                        Utils.shareImg(file, Utils.getTypeFromImgUrl(getCurrentImage()), GalleryActivity.this);
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
