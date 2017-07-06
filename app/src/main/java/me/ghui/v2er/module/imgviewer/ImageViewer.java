package me.ghui.v2er.module.imgviewer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.TextView;


import me.ghui.v2er.R;
import me.ghui.v2er.general.Navigator;
import me.ghui.v2er.util.Utils;

public class ImageViewer extends FragmentActivity {
    public static final String EXTRA_IMG_DATA = Utils.KEY("extra_img_data");
    public static final String STATE_POSITION = Utils.KEY("state_position");
    public static ImagesInfo imgsData;
    private ViewPager mPager;
    private int pagerPosition;
    private TextView indicator;

    public static void open(ImagesInfo imgsData, Context context) {
        Navigator.from(context)
                .putExtra(EXTRA_IMG_DATA, imgsData)
                .to(ImageViewer.class)
                .start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.fullScreen(getWindow());
        setContentView(R.layout.image_detail_pager);
        imgsData = (ImagesInfo) getIntent().getSerializableExtra(EXTRA_IMG_DATA);
        pagerPosition = imgsData.getPosition();

        mPager = (ViewPager) findViewById(R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(
                getSupportFragmentManager(), imgsData.getImages());
        mPager.setOffscreenPageLimit(1);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);
        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
                .getAdapter().getCount());
        indicator.setText(text);
        // 更新下标
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ImagesInfo.Images fileList;

        public ImagePagerAdapter(FragmentManager fm, ImagesInfo.Images fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position).getUrl();
            return ImageDetailFragment.newInstance(url);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

    }
}
