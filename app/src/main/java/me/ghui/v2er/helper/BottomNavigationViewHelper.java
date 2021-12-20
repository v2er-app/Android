package me.ghui.v2er.helper;

import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {

    /**
     * 设置图片尺寸
     * @param view
     * @param width
     * @param height
     */
    public static void setImageSize(BottomNavigationView view, int width, int height) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                ImageView imageView = item.findViewById(com.google.android.material.R.id.icon);
                imageView.getLayoutParams().width = width;
                imageView.getLayoutParams().height = height;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}