package me.ghui.v2er.helper;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.view.menu.MenuView;

import com.flyco.tablayout.widget.MsgView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import me.ghui.v2er.R;
import me.ghui.v2er.util.UnreadMsgUtils;

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

    public static void showBadgeView(Context context, BottomNavigationView bottomNavigationView, int viewIndex,
                                    int showNumber) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(viewIndex);
        ImageView itemIcon = itemView.findViewById(R.id.icon);
        final View badgeView = LayoutInflater.from(context).inflate(R.layout.message_count_layout,
                itemView, false);
        MsgView mcMsgView = badgeView.findViewById(R.id.mcMsgView);
        final FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(badgeView.getLayoutParams());
        itemIcon.post(() -> {
            frameLayoutParams.gravity = Gravity.TOP | Gravity.END;
            UnreadMsgUtils.show(context, mcMsgView, showNumber, width -> {
                frameLayoutParams.rightMargin = itemView.getWidth() / 2 - width;//图片的宽度的一半
                itemView.addView(badgeView, frameLayoutParams);
            });
        });
    }

    public static void hideMsg(Context context, BottomNavigationView bottomNavigationView, int viewIndex) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(viewIndex);
        int childCount = itemView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = itemView.getChildAt(i);
            if (childView instanceof FrameLayout) {
                itemView.removeView(childView);
                break;
            }
        }
    }

}