package me.ghui.v2er.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyco.tablayout.widget.MsgView;

import me.ghui.v2er.R;

public class UnreadMsgUtils {

    public interface Callback {
        void onShow(int width, int height);
    }

    public static void show(Context context, MsgView msgView, int num, Callback callback) {
        float textSize = context.getResources().getDimension(R.dimen.tinyTextSize);
        msgView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        msgView.setCornerRadius((int) (msgView.getTextSize()) / 2);
        msgView.setGravity(Gravity.CENTER);
        int width = 0, height = 0;
        if (num < 0) {
            num = 0;
        }
        String showNumberStr;
        height = (int) (msgView.getTextSize() * 1.5);
        if (num < 10) {
            showNumberStr = String.valueOf(num);
            width = (int) (msgView.getTextSize() * 1.5);
        } else if (num < 100){
            showNumberStr = String.valueOf(num);
            width = (int) (msgView.getTextSize() * 2.5);
        } else {
            showNumberStr = "•••";
            width = (int) (msgView.getTextSize() * 2.5);
        }
        msgView.setWidth(width);
        msgView.setHeight(height);
        msgView.setText(showNumberStr);
        if (callback != null) {
            callback.onShow(width, height);
        }
    }

}
